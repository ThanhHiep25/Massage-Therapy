package com.example.spa.servicesImpl;

import com.example.spa.entities.Position;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.PositionRepository;
import com.example.spa.services.PositionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<Position> getAllPosition() {
        return positionRepository.findAll();
    }

    @Override
    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POSITION_INVALID));
    }

    @Override
    public Position createPosition(Position position) {
        if (positionExists(position.getPositionId())) {
            throw new AppException(ErrorCode.POSITION_ALREADY_EXISTED);
        }
        return positionRepository.save(position);
    }

    @Override
    public void deletePosition(Long id) {
        if (!positionExists(id)) {
            throw new AppException(ErrorCode.POSITION_INVALID);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public Position updatePosition(Long id, Position position) {
        Position positions = getPositionById(id);
        positions.setPositionName(position.getPositionName());
        positions.setDescription(position.getDescription());
        return positionRepository.save(positions);
    }

    @Override
    public boolean positionExists(Long id) {
        return positionRepository.existsById(id);
    }

    @Override
    public Position findByName(String name) {
        return positionRepository.findByPositionName(name).orElse(null);
    }



    @Override
    public List<Position> importPostionFromJson(String json) {
        try {
            List<Position> positions = objectMapper.readValue(json, new TypeReference<List<Position>>() {});
            return positions.stream()
                    .map(positionRepository::save)
                    .toList();
        } catch (Exception e) {
            throw new AppException(ErrorCode.POSITION_INVALID);
        }
    }

    @Override
    public List<Position> importPositionsFromFile(MultipartFile file) throws IOException {
        try {
            List<Position> positions = objectMapper.readValue(file.getInputStream(), new TypeReference<List<Position>>() {});
            return positions.stream()
                    .map(positionRepository::save)
                    .toList();
        } catch (IOException e) {
            throw new AppException(ErrorCode.POSITION_INVALID);
        }
    }
}
