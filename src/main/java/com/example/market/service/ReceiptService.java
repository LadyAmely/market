package com.example.market.service;

import com.example.market.dto.response.ProductResponse;
import com.example.market.dto.response.ReceiptResponse;
import com.example.market.model.Product;
import com.example.market.model.Receipt;
import com.example.market.model.ReceiptItem;
import com.example.market.repository.ReceiptRepository;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private static final PageSize RECEIPT_PAGE_SIZE = new PageSize(226.77f, 850f);
    private static final float MARGIN = 10f;
    private static final int FONT_SIZE = 9;
    private static final int TOTAL_FONT_SIZE = 14;
    private static final float BARCODE_WIDTH_PT = 180f;
    private static final float BARCODE_HEIGHT_PT = 40f;

    @Transactional
    public byte[] finalizeReceipt(String paymentMethod, List<ProductResponse> scannedItems) {
        if (scannedItems == null || scannedItems.isEmpty()) {
            throw new IllegalStateException("No scanned products to finalize receipt.");
        }

        BigDecimal totalNet = calculateTotalNet(scannedItems);
        BigDecimal totalVat = calculateTotalVat(scannedItems);
        BigDecimal totalGross = totalNet.add(totalVat);

        Receipt receipt = buildReceipt(paymentMethod, totalNet, totalVat, totalGross);
        List<ReceiptItem> items = buildReceiptItems(receipt, scannedItems);
        receipt.setItems(items);

        Receipt saved = receiptRepository.save(receipt);
        ReceiptResponse response = buildReceiptResponse(saved, scannedItems);

        return generateReceiptPdf(response);
    }

    private byte[] generateReceiptPdf(ReceiptResponse receipt) {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdf = new PdfDocument(writer)
        ) {
            Document doc = new Document(pdf, RECEIPT_PAGE_SIZE);
            doc.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
            doc.setFont(font).setFontSize(FONT_SIZE);

            addStoreHeader(doc, receipt);
            addReceiptDetails(doc, receipt);
            addProductLines(doc, receipt.items());
            addTaxSummary(doc, receipt);
            addTotal(doc, receipt.totalGross());
            addTransactionDetails(doc, receipt);
            addThankYouMessage(doc);
            addBarcode(doc, pdf, receipt.receiptId().toString());

            doc.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation error: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculateTotalNet(List<ProductResponse> scannedItems) {
        return scannedItems.stream()
                .map(ProductResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalVat(List<ProductResponse> scannedItems) {
        return scannedItems.stream()
                .map(item -> {
                    try {
                        String rate = item.vatRate().replace("%", "");
                        BigDecimal vat = new BigDecimal(rate).divide(BigDecimal.valueOf(100));
                        return item.totalPrice().multiply(vat);
                    } catch (NumberFormatException e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void addStoreHeader(Document doc, ReceiptResponse receipt) {
        var store = receipt.store();
        var location = store.getLocation();

        doc.add(new Paragraph(store.getStoreName()).setBold().setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(location.getStreetAddress() + ", " + location.getPostalCode() +
                " " + location.getCity()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(store.getCompanyName()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(store.getCompanyAddress()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("NIP " + receipt.nip()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(" "));
    }

    private void addReceiptDetails(Document doc, ReceiptResponse receipt) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        doc.add(new Paragraph("Data: " + receipt.dateTime().format(formatter)).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Nr paragonu: " + receipt.receiptNumber()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Kasjer: " + receipt.cashier()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Terminal: " + receipt.terminal().getName()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("PARAGON FISKALNY").setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" "));
    }

    private void addProductLines(Document doc, List<ProductResponse> items) {
        for (ProductResponse item : items) {
            doc.add(new Paragraph(item.name().toUpperCase()).setTextAlignment(TextAlignment.LEFT));
            doc.add(new Paragraph("*" + item.totalPrice() + " - " + item.totalPrice() + " A").setTextAlignment(TextAlignment.LEFT));
            doc.add(new Paragraph(" "));
        }
    }

    private void addTaxSummary(Document doc, ReceiptResponse receipt) {
        doc.add(new Paragraph("Sprzed. opod. PTU A").setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Kwota A 23,00%").setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Podatek PTU: " + receipt.totalVat() + " PLN").setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(" "));
    }

    private void addTotal(Document doc, BigDecimal totalGross) {
        doc.add(new Paragraph("SUMA PLN  " + String.format("%.2f", totalGross))
                .setFontSize(TOTAL_FONT_SIZE)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(" "));
    }

    private void addTransactionDetails(Document doc, ReceiptResponse receipt) {
        doc.add(new Paragraph("Terminal ID: " + receipt.terminal().getId()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Płatność: " + receipt.paymentMethod()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("Numer transakcji: " + receipt.transactionNumber()).setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph(" "));
    }

    private void addThankYouMessage(Document doc) {
        doc.add(new Paragraph("*** THANKS FOR YOUR VISIT ***")
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" "));
    }

    private void addBarcode(Document doc, PdfDocument pdf, String code) {
        Barcode128 barcode = new Barcode128(pdf);
        barcode.setCode(code);
        barcode.setCodeType(Barcode128.CODE128);
        PdfFormXObject barcodeObject = barcode.createFormXObject(ColorConstants.BLACK, ColorConstants.WHITE, pdf);
        Image barcodeImage = new Image(barcodeObject);
        barcodeImage.setWidth(BARCODE_WIDTH_PT);
        barcodeImage.setHeight(BARCODE_HEIGHT_PT );
        barcodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
        doc.add(barcodeImage);
    }

    private Receipt buildReceipt(String paymentMethod, BigDecimal totalNet, BigDecimal totalVat, BigDecimal totalGross) {
        return Receipt.builder()
                .dateTime(LocalDateTime.now())
                .paymentType(paymentMethod)
                .totalNet(totalNet)
                .totalVat(totalVat)
                .totalGross(totalGross)
                .build();
    }

    private List<ReceiptItem> buildReceiptItems(Receipt receipt, List<ProductResponse> scannedItems) {
        return scannedItems.stream()
                .map(product -> ReceiptItem.builder()
                        .receipt(receipt)
                        .product(Product.builder().id(product.productId()).build())
                        .quantity(product.quantity())
                        .unitPrice(product.unitPrice())
                        .discount(product.discount())
                        .vatRate(product.vatRate())
                        .totalPrice(product.totalPrice())
                        .build())
                .toList();
    }

    private ReceiptResponse buildReceiptResponse(Receipt saved, List<ProductResponse> scannedItems) {
        return ReceiptResponse.builder()
                .receiptId(saved.getId())
                .dateTime(saved.getDateTime())
                .items(List.copyOf(scannedItems))
                .totalNet(saved.getTotalNet())
                .totalVat(saved.getTotalVat())
                .totalGross(saved.getTotalGross())
                .paymentMethod(saved.getPaymentType())
                .build();
    }
}
