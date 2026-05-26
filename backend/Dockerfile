# This will take from 10 to 40 min, depends on hardware and docker configuration
FROM container-registry.oracle.com/graalvm/native-image:21 AS builder

WORKDIR /app

COPY . .

WORKDIR /app/backend

RUN chmod +x mvnw

RUN ./mvnw -Pnative -DskipTests native:compile

FROM container-registry.oracle.com/graalvm/native-image:21
WORKDIR /app

COPY --from=builder /app/backend/target/salonshub /app/salonshub

RUN chmod +x /app/salonshub

EXPOSE 8080

ENTRYPOINT ["/app/salonshub"]