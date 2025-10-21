FROM ubuntu:latest
LABEL authors="amelia"

ENTRYPOINT ["top", "-b"]