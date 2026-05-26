FROM container-registry.oracle.com/graalvm/native-image:21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw -Pnative -DskipTests native:compile

FROM container-registry.oracle.com/graalvm/native-image:21
WORKDIR /app

COPY --from=builder /app/target/salonshub /app/salonshub

RUN chmod +x /app/salonshub

EXPOSE 8080

ENTRYPOINT ["/app/salonshub"]