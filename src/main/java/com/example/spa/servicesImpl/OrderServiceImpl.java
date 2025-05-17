package com.example.spa.servicesImpl;

import com.example.spa.dto.request.CreateOrderRequest;
import com.example.spa.dto.response.*;
import com.example.spa.entities.*;
import com.example.spa.enums.OrderStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.OrderItemRepository;
import com.example.spa.repositories.OrderRepository;
import com.example.spa.repositories.ProductRepository;
import com.example.spa.repositories.UserRepository;
import com.example.spa.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    //    @Override
//    @Transactional
//    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
//        User user = userRepository.findById(createOrderRequest.getUserId())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        Order order = Order.builder()
//                .shippingAddress(createOrderRequest.getShippingAddress())
//                .shippingPhone(createOrderRequest.getShippingPhone())
//                .notes(createOrderRequest.getNotes())
//                .build();
//
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        BigDecimal finalTotalAmount = totalAmount;
//        List<OrderItem> orderItems = createOrderRequest.getOrderItems().stream()
//                .map(itemRequest -> {
//                    Product product = productRepository.findById(itemRequest.getProductId())
//                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
//                    BigDecimal price = product.getPrice();
//                    BigDecimal subTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
//                    finalTotalAmount.add(subTotal); // Cẩn thận với việc cộng trực tiếp trong stream
//                    return OrderItem.builder()
//                            .order(order)
//                            .product(product)
//                            .quantity(itemRequest.getQuantity())
//                            .price(price)
//                            .subTotal(subTotal)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        order.setOrderItems(orderItems);
//        // Tính toán lại totalAmount sau khi tạo OrderItems để tránh vấn đề với stream
//        totalAmount = orderItems.stream()
//                .map(OrderItem::getSubTotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        order.setTotalAmount(totalAmount);
//        order.setOrderDate(LocalDateTime.now());
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//        order.setStatus(OrderStatus.PENDING);
//
//        Order savedOrder = orderRepository.save(order);
//
//        return mapOrderToOrderResponse(savedOrder);
//    }

    // Xây dựng order bằng stream
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        Order.OrderBuilder orderBuilder = Order.builder()
                .shippingAddress(createOrderRequest.getShippingAddress())
                .shippingPhone(createOrderRequest.getShippingPhone())
                .notes(createOrderRequest.getNotes())
                .orderDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(OrderStatus.PENDING);

        // Kiểm tra nếu có userId thì gán User, nếu không thì xử lý khách hàng ẩn danh
        if (createOrderRequest.getUserId() != null) {
            User user = userRepository.findById(createOrderRequest.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            orderBuilder.user(user);
        } else {
            // Xử lý khách hàng ẩn danh
            String guestName = (createOrderRequest.getGuestName() == null || createOrderRequest.getGuestName().trim().isEmpty())
                    ? "Khách hàng ẩn danh"
                    : createOrderRequest.getGuestName();
            orderBuilder.guestName(guestName);
        }

        Order order = orderBuilder.build();

        // Tính toán tổng tiền và tạo OrderItems
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal finalTotalAmount = totalAmount;
        List<OrderItem> orderItems = createOrderRequest.getOrderItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                    BigDecimal price = product.getPrice();
                    BigDecimal subTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                    finalTotalAmount.add(subTotal); // Cẩn thận với việc cộng trực tiếp trong stream
                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemRequest.getQuantity())
                            .price(price)
                            .subTotal(subTotal)
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        totalAmount = orderItems.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        // Lưu đơn hàng vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(order);
        return mapOrderToOrderResponse(savedOrder);
    }

    // Update đơn hàng
    @Transactional
    @Override
    public OrderResponse updateOrder(Long id, CreateOrderRequest updateRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật thông tin đơn hàng
        order.setShippingAddress(updateRequest.getShippingAddress());
        order.setShippingPhone(updateRequest.getShippingPhone());
        order.setNotes(updateRequest.getNotes());
        //order.setUpdatedAt(LocalDateTime.now());

        // Cập nhật danh sách sản phẩm trong đơn hàng
//        List<OrderItem> updatedOrderItems = updateRequest.getOrderItems().stream()
//                .map(itemRequest -> {
//                    Product product = productRepository.findById(itemRequest.getProductId())
//                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
//                    BigDecimal price = product.getPrice();
//                    BigDecimal subTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
//                    return OrderItem.builder()
//                            .order(order)
//                            .product(product)
//                            .quantity(itemRequest.getQuantity())
//                            .price(price)
//                            .subTotal(subTotal)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // Xóa các OrderItem cũ và thêm các OrderItem mới
//        order.getOrderItems().clear();
//        for (OrderItem item : updatedOrderItems) {
//            item.setOrder(order); // đảm bảo mối quan hệ 2 chiều
//        }
//        order.getOrderItems().addAll(updatedOrderItems);
//
//        // Tính toán lại tổng tiền
//        BigDecimal totalAmount = updatedOrderItems.stream()
//                .map(OrderItem::getSubTotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        order.setTotalAmount(totalAmount);

        // Lưu đơn hàng đã cập nhật
        Order updatedOrder = orderRepository.save(order);
        return mapOrderToOrderResponse(updatedOrder);
    }

    // Lấy đơn hàng bằng id
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return mapOrderToOrderResponse(order);
    }

    // Lấy danh sách đơn hàng bằng userId
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return getOrdersByUser(user); // Gọi phương thức hiện có
    }

    // Lấy danh sách đơn hàng bằng User
    @Override
    public List<OrderResponse> getOrdersByUser(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

    // Lấy tất cả đơn hàng
    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật tràng thái đơn hàng
    @Override
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
        Order updatedOrder = orderRepository.save(order);
        return mapOrderToOrderResponse(updatedOrder);
    }

    // Xóa đơn hàng
    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }

    // Cập nhật trạng thái Processing
    @Override
    public void processOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    // Cập nhật trạng thái SHIPPED
    @Override
    public void shippedOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    // Cập nhật trạng thái DELIVERED
    @Override
    public void deliveredOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    // Cập nhật trạng thái CANCELLED
    @Override
    public void cancelledOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Cập nhật trạng thái PAID
    @Override
    public void paidOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    // Cập nhật trạng thái REFUND
    @Override
    public void refundOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.REFUND);
        orderRepository.save(order);
    }

    @Override
    public byte[] exportOrdersToExcel(List<OrderResponse> orders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        // Style cho header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);

        // Style cho data
        CellStyle dataStyle = workbook.createCellStyle();
        setBorder(dataStyle);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã đơn hàng", "Người dùng", "Khách hàng", "Ngày đặt hàng", "Tổng tiền", "Trạng thái", "Địa chỉ giao hàng", "Số điện thoại giao hàng", "Ghi chú", "Sản phẩm", "Ngày tạo", "Ngày cập nhật"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (OrderResponse order : orders) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, order.getId(), dataStyle);
            createCell(row, 1, order.getUser() != null ? order.getUser().getName() : "Trống", dataStyle);
            createCell(row, 2, order.getGuestName() != null ? order.getGuestName() : "Trống", dataStyle);
            createCell(row, 3, order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "", dataStyle);
            createCell(row, 4, order.getTotalAmount() != null ? order.getTotalAmount().toString() : "0.0", dataStyle);
            createCell(row, 5, order.getStatus() != null ? order.getStatus().name() : "", dataStyle);
            createCell(row, 6, order.getShippingAddress() != null ? order.getShippingAddress() : "Trống", dataStyle);
            createCell(row, 7, order.getShippingPhone() != null ? order.getShippingPhone() : "Trống", dataStyle);
            createCell(row, 8, order.getNotes() != null ? order.getNotes() : "Trống", dataStyle);

            // Liệt kê các sản phẩm trong đơn hàng
            String productNames = order.getOrderItems().stream()
                    .map(item -> item.getProduct().getNameProduct() + " (" + item.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            createCell(row, 9, productNames, dataStyle);

            createCell(row, 10, order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "", dataStyle);
            createCell(row, 11, order.getUpdatedAt() != null ? order.getUpdatedAt().format(formatter) : "", dataStyle);
        }

        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }


    private OrderResponse mapOrderToOrderResponse(Order order) {
        UserResponse userResponse = order.getUser() != null
                ? new UserResponse(order.getUser())
                : null;

        return OrderResponse.builder()
                .id(order.getId())
                .user(userResponse)
                .guestName(order.getGuestName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .shippingPhone(order.getShippingPhone())
                .notes(order.getNotes())
                .orderItems(order.getOrderItems().stream()
                        .map(this::mapOrderItemToOrderItemResponse)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapOrderItemToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .product(mapProductToProductResponse(orderItem.getProduct()))
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subTotal(orderItem.getSubTotal())
                .build();
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .nameProduct(product.getNameProduct())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(mapCategoryToCategoryResponse(product.getCategory()))
                .imageUrl(product.getImageUrl())
                .quantity(product.getQuantity())
                .productStatus(product.getProductStatus())
                .build();
    }

    private CategoryResponse mapCategoryToCategoryResponse(Categories category) {
        if (category == null) return null;
        return CategoryResponse.builder()
                .id(category.getCategoryId())
                .name(category.getCategoryName())
                .build();
    }

    private UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getUserId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .phone(user.getPhone())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }



}