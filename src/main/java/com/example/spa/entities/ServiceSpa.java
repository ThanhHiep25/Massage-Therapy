package com.example.spa.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
service_id: ID duy nhất của dịch vụ.
name: Tên dịch vụ (ví dụ: "Massage đá nóng").
description: Mô tả chi tiết về dịch vụ.
price: Giá dịch vụ.
duration: Thời gian thực hiện (ví dụ: 60 phút, 90 phút).
image_url: Đường dẫn ảnh minh họa dịch vụ.
service_type: Loại dịch vụ (thư giãn, trị liệu, thể thao, v.v.).
*/
@Entity
@Table(name = "service_spa")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long service_id;

    @Column(name = "service_name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "duration")
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories categories;


    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "service_type")
    private String service_type;

    @ManyToMany(mappedBy = "services")
    private List<Staff> staff = new ArrayList<>();


    @OneToMany(mappedBy = "serviceSpa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ServiceStep> steps = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Override
    public String toString() {
        return "ServiceSpa{" +
                "service_id=" + service_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", categories=" + categories +
                ", imageUrl='" + imageUrl + '\'' +
                ", service_type='" + service_type + '\'' +
                '}';
    }
}



