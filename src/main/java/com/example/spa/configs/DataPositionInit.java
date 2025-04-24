//package com.example.spa.configs;
//
//import com.example.spa.entities.Position;
//import com.example.spa.repositories.PositionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataPositionInit implements CommandLineRunner {
//
//    @Autowired
//    private PositionRepository positionRepository;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        if (positionRepository.count() == 0){
//            Position quanLyposition = new Position();
//            quanLyposition.setPositionName("Quản lý");
//            quanLyposition.setDescription("Quản lý Spa và nhân viên");
//            positionRepository.save(quanLyposition);
//
//            Position nhanVienposition = new Position();
//            nhanVienposition.setPositionName("Nhân viên");
//            nhanVienposition.setDescription("Nhân viên Spa");
//            positionRepository.save(nhanVienposition);
//
//            Position truongNhomposition = new Position();
//            truongNhomposition.setPositionName("Trưởng nhóm");
//            truongNhomposition.setDescription("Trưởng nhóm Spa");
//            positionRepository.save(truongNhomposition);
//
//            System.out.println("Positions initialized: Quản lý, Nhân viên, Trưởng nhóm");
//        }
//    }
//}
//
//
