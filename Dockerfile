# Sử dụng hình ảnh Java chính thức
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file JAR vào container
COPY target/spa-massage-server-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng mà ứng dụng sẽ chạy
EXPOSE 5000

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
