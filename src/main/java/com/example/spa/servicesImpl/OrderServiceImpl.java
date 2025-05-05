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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return mapOrderToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return getOrdersByUser(user); // Gọi phương thức hiện có
    }

    @Override
    public List<OrderResponse> getOrdersByUser(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

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

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
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