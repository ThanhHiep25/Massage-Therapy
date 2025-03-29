package com.example.spa.services;

import com.example.spa.entities.Position;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PositionService {
    List<Position> getAllPosition();
    Position getPositionById(Long id);
    Position createPosition(Position position);
    void deletePosition(Long id);
    Position updatePosition(Long id, Position position);
    boolean positionExists(Long id);
    Position findByName(String name);
    List<Position> importPostionFromJson(String json);
    List<Position> importPositionsFromFile(MultipartFile file) throws IOException;
}
