package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.repository.UniversiteRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversiteServiceImplTest {

    @Mock // Create a fake version of the UniversiteRepository
    private UniversiteRepository universiteRepository;

    @InjectMocks // Create a real UniversiteServiceImpl and inject the fake repository
    private UniversiteServiceImpl universiteService;

    @Test
    void testRetrieveAllUniversites() {
        // Arrange
        Universite u1 = new Universite(1L, "Université de Tunis El Manar", null);
        Universite u2 = new Universite(2L, "Université de Carthage", null);
        when(universiteRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Université de Tunis El Manar", result.get(0).getNomUniversite());
    }

    @Test
    void testRetrieveUniversite_Success() {
        // Arrange
        long universiteId = 1L;
        Universite mockUniversite = new Universite(universiteId, "Université de Sousse", null);
        when(universiteRepository.findById(universiteId)).thenReturn(Optional.of(mockUniversite));

        // Act
        Universite result = universiteService.retrieveUniversite(universiteId);

        // Assert
        assertNotNull(result);
        assertEquals(universiteId, result.getIdUniversite());
        assertEquals("Université de Sousse", result.getNomUniversite());
    }

    @Test
    void testRetrieveUniversite_NotFound_ShouldThrowException() {
        // Arrange
        long nonExistentId = 99L;
        when(universiteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        // This is a critical test to ensure your code handles cases where data is not found.
        // It confirms that calling .get() on an empty Optional throws the correct exception.
        assertThrows(NoSuchElementException.class, () -> {
            universiteService.retrieveUniversite(nonExistentId);
        });
    }

    @Test
    void testAddUniversite() {
        // Arrange
        Universite newUniversite = new Universite(null, "Université de Monastir", null);
        Universite savedUniversite = new Universite(5L, "Université de Monastir", null);
        when(universiteRepository.save(any(Universite.class))).thenReturn(savedUniversite);

        // Act
        Universite result = universiteService.addUniversite(newUniversite);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getIdUniversite()); // Check that the ID from the saved object is returned
    }

    @Test
    void testModifyUniversite() {
        // Arrange
        Universite existingUniversite = new Universite(1L, "Université de Sfax - Updated", null);
        when(universiteRepository.save(existingUniversite)).thenReturn(existingUniversite);

        // Act
        Universite result = universiteService.modifyUniversite(existingUniversite);

        // Assert
        assertNotNull(result);
        assertEquals("Université de Sfax - Updated", result.getNomUniversite());
        // Verify that the save method was called on the repository
        verify(universiteRepository, times(1)).save(existingUniversite);
    }

    @Test
    void testRemoveUniversite() {
        // Arrange
        long universiteIdToRemove = 1L;
        // Mocking a void method: we just ensure it can be called without error
        doNothing().when(universiteRepository).deleteById(universiteIdToRemove);

        // Act
        universiteService.removeUniversite(universiteIdToRemove);

        // Assert (by verifying the interaction)
        // For a void method, the test is to verify that the correct method on the dependency was called.
        verify(universiteRepository, times(1)).deleteById(universiteIdToRemove);
    }
}