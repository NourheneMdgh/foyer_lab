package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.repository.ChambreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChambreServiceImplTest {

    @Mock // We create a fake version of the repository
    private ChambreRepository chambreRepository;

    @InjectMocks // We create a real instance of the service and inject the fake repository
    private ChambreServiceImpl chambreService;

    @Test
    void testRetrieveAllChambres() {
        // Arrange: Prepare mock data and define mock behavior
        List<Chambre> mockChambres = new ArrayList<>();
        mockChambres.add(new Chambre(1L, 101L, TypeChambre.SIMPLE, null));
        mockChambres.add(new Chambre(2L, 102L, TypeChambre.DOUBLE, null));
        when(chambreRepository.findAll()).thenReturn(mockChambres);

        // Act: Call the method being tested
        List<Chambre> result = chambreService.retrieveAllChambres();

        // Assert: Check the results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getNumeroChambre());
    }

    @Test
    void testRetrieveChambre_Success() {
        // Arrange
        long chambreId = 1L;
        Chambre mockChambre = new Chambre(chambreId, 101L, TypeChambre.SIMPLE, null);
        when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(mockChambre));

        // Act
        Chambre result = chambreService.retrieveChambre(chambreId);

        // Assert
        assertNotNull(result);
        assertEquals(chambreId, result.getIdChambre());
        assertEquals(101L, result.getNumeroChambre());
    }

    @Test
    void testRetrieveChambre_NotFound_ShouldThrowException() {
        // Arrange
        long nonExistentId = 99L;
        // Mock the repository to return an empty Optional, simulating "not found"
        when(chambreRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        // Verify that calling .get() on an empty Optional throws NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> {
            chambreService.retrieveChambre(nonExistentId);
        }, "Should throw NoSuchElementException when chambre is not found");
    }

    @Test
    void testAddChambre() {
        // Arrange
        Chambre newChambre = new Chambre(null, 201L, TypeChambre.TRIPLE, null); // ID is null before save
        Chambre savedChambre = new Chambre(5L, 201L, TypeChambre.TRIPLE, null); // ID is set after save
        when(chambreRepository.save(any(Chambre.class))).thenReturn(savedChambre);

        // Act
        Chambre result = chambreService.addChambre(newChambre);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getIdChambre()); // Check if the ID from the saved object is returned
        assertEquals(201L, result.getNumeroChambre());
    }

    @Test
    void testModifyChambre() {
        // Arrange
        Chambre chambreToModify = new Chambre(1L, 101L, TypeChambre.DOUBLE, null);
        // Let's assume the save operation returns the same object
        when(chambreRepository.save(chambreToModify)).thenReturn(chambreToModify);

        // Act
        Chambre result = chambreService.modifyChambre(chambreToModify);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdChambre());
        assertEquals(TypeChambre.DOUBLE, result.getTypeC());
        // Verify that the save method was called exactly once
        verify(chambreRepository, times(1)).save(chambreToModify);
    }

    @Test
    void testRemoveChambre() {
        // Arrange
        long chambreIdToRemove = 1L;
        // For void methods, we don't need to define a return value.
        // We just need to ensure the method is callable on the mock.
        doNothing().when(chambreRepository).deleteById(chambreIdToRemove);

        // Act
        chambreService.removeChambre(chambreIdToRemove);

        // Assert / Verify
        // The most important test for a void method is to verify the interaction.
        // We check that the deleteById method was called exactly once with the correct ID.
        verify(chambreRepository, times(1)).deleteById(chambreIdToRemove);
    }

    @Test
    void testRecupererChambresSelonTyp() {
        // Arrange
        TypeChambre targetType = TypeChambre.DOUBLE;
        List<Chambre> mockChambres = new ArrayList<>();
        mockChambres.add(new Chambre(2L, 102L, targetType, null));
        mockChambres.add(new Chambre(3L, 202L, targetType, null));
        when(chambreRepository.findAllByTypeC(targetType)).thenReturn(mockChambres);

        // Act
        List<Chambre> result = chambreService.recupererChambresSelonTyp(targetType);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        // Check that all returned chambres are of the correct type
        assertTrue(result.stream().allMatch(c -> c.getTypeC() == targetType));
        // Verify the repository method was called
        verify(chambreRepository, times(1)).findAllByTypeC(targetType);
    }

    @Test
    void testTrouverChambreSelonEtudiant() {
        // Arrange
        long studentCin = 12345678L;
        Chambre mockChambre = new Chambre(10L, 301L, TypeChambre.SIMPLE, null);
        when(chambreRepository.trouverChselonEt(studentCin)).thenReturn(mockChambre);

        // Act
        Chambre result = chambreService.trouverchambreSelonEtudiant(studentCin);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getIdChambre());
        verify(chambreRepository, times(1)).trouverChselonEt(studentCin);
    }
}