package org.example.service;


/*   Crea un nuevo producto validando que el SKU no exista previamente.
Insert*/
/*  Actualiza los datos de un producto existente (si existe, y sin duplicar SKU).
Update */
/*  Obtiene un producto por su ID. Lanza excepción si no existe.
Select (por ID) */
/*  Busca un producto por su SKU.
Select (por campo único) */
/*  Devuelve una lista de productos cuyo nombre contenga un texto parcial.
Select (filtro LIKE) */
/*  Elimina un producto por su ID (si existe).
Delete */

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductoDTO;
import org.example.model.Producto;
import org.example.repository.ProductoRepository;
import org.example.service.exception.BusinessException;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoDTO create(ProductoDTO dto) {
        normalize(dto);
        if (dto.getSku() != null && productoRepository.existsBySku(dto.getSku())) {
            throw new BusinessException("Ya existe un producto con SKU " + dto.getSku());
        }
        Producto entity = toEntity(dto);
        entity.setId(null);
        Producto saved = productoRepository.save(entity);
        return toDTO(saved);
    }

    public ProductoDTO update(Long id, ProductoDTO dto) {
        Producto current = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto id=" + id + " no encontrado"));

        normalize(dto);
        if (dto.getSku() != null && !dto.getSku().equals(current.getSku())
                && productoRepository.existsBySku(dto.getSku())) {
            throw new BusinessException("Ya existe un producto con SKU " + dto.getSku());
        }

        current.setSku(dto.getSku());
        current.setNombre(dto.getNombre());
        current.setPrincipioActivo(dto.getPrincipioActivo());
        current.setUnidad(dto.getUnidad());
        current.setPresentacion(dto.getPresentacion());
        current.setIvaPorcentaje(dto.getIvaPorcentaje());
        current.setEsControlado(dto.getEsControlado());
        current.setEsOtc(dto.getEsOtc());

        return toDTO(productoRepository.save(current));
    }

    @Transactional(readOnly = true)
    public ProductoDTO getById(Long id) {
        return productoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Producto id=" + id + " no encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<ProductoDTO> getBySku(String sku) {
        String key = sku == null ? null : sku.trim().toUpperCase();
        return productoRepository.findBySku(key).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> searchByNombre(String query) {
        String q = query == null ? "" : query.trim();
        return productoRepository.findByNombreContainingIgnoreCase(q)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto id=" + id + " no encontrado");
        }
        productoRepository.deleteById(id);
    }

    // ---- helpers ----
    private void normalize(ProductoDTO dto) {
        if (dto.getSku() != null) dto.setSku(dto.getSku().trim().toUpperCase());
        if (dto.getNombre() != null) dto.setNombre(dto.getNombre().trim());
    }

    private ProductoDTO toDTO(Producto p) {
        return ProductoDTO.builder()
                .id(p.getId())
                .sku(p.getSku())
                .nombre(p.getNombre())
                .principioActivo(p.getPrincipioActivo())
                .unidad(p.getUnidad())
                .presentacion(p.getPresentacion())
                .ivaPorcentaje(p.getIvaPorcentaje())
                .esControlado(p.getEsControlado())
                .esOtc(p.getEsOtc())
                .build();
    }

    private Producto toEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setId(dto.getId());
        p.setSku(dto.getSku());
        p.setNombre(dto.getNombre());
        p.setPrincipioActivo(dto.getPrincipioActivo());
        p.setUnidad(dto.getUnidad());
        p.setPresentacion(dto.getPresentacion());
        p.setIvaPorcentaje(dto.getIvaPorcentaje());
        p.setEsControlado(dto.getEsControlado());
        p.setEsOtc(dto.getEsOtc());
        return p;
    }
}
