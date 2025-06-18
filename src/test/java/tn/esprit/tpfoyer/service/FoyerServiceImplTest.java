package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.repository.FoyerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoyerServiceImplTest {

    @Mock // Create a mock of the repository dependency
    private FoyerRepository foyerRepository;

    @InjectMocks // Create a real FoyerServiceImpl and inject the mock repository into it
    private FoyerServiceImpl foyerService;

    @Test
    void testRetrieveAllFoyers() {
        // Arrange: Set up the mock's behavior
        Foyer foyer1 = new Foyer(1L, "Foyer El Ghazela", 100L, null);
        Foyer foyer2 = new Foyer(2L, "Foyer Charguia", 150L, null);
        when(foyerRepository.findAll()).thenReturn(List.of(foyer1, foyer2));

        // Act: Call the method under test
        List<Foyer> result = foyerService.retrieveAllFoyers();

        // Assert: Verify the outcome
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Foyer El Ghazela", result.get(0).getNomFoyer());
    }

    @Test
    void testRetrieveFoyer_Success() {
        // Arrange
        long foyerId = 1L;
        Foyer mockFoyer = new Foyer(foyerId, "Foyer Test", 100L, null);
        when(foyerRepository.findById(foyerId)).thenReturn(Optional.of(mockFoyer));

        // Act
        Foyer result = foyerService.retrieveFoyer(foyerId);

        // Assert
        assertNotNull(result);
        assertEquals(foyerId, result.getIdFoyer());
    }

    @Test
    void testRetrieveFoyer_NotFound_ShouldThrowException() {
        // Arrange
        long nonExistentId = 99L;
        when(foyerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        // This is a critical test. It confirms that calling .get() on an empty Optional
        // correctly throws an exception, preventing a NullPointerException.
        assertThrows(NoSuchElementException.class, () -> {
            foyerService.retrieveFoyer(nonExistentId);
        }, "An exception should be thrown when the foyer ID does not exist.");
    }

    @Test
    void testAddFoyer() {
        // Arrange
        Foyer foyerToSave = new Foyer(null, "New Foyer", 200L, null);
        Foyer savedFoyer = new Foyer(10L, "New Foyer", 200L, null);
        when(foyerRepository.save(any(Foyer.class))).thenReturn(savedFoyer);

        // Act
        Foyer result = foyerService.addFoyer(foyerToSave);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getIdFoyer()); // Important to check the ID from the "saved" object
    }

    @Test
    void testModifyFoyer() {
        // Arrange
        Foyer foyerToModify = new Foyer(1L, "Updated Foyer Name", 120L, null);
        // We tell the mock to return the same object when save is called
        when(foyerRepository.save(foyerToModify)).thenReturn(foyerToModify);

        // Act
        Foyer result = foyerService.modifyFoyer(foyerToModify);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Foyer Name", result.getNomFoyer());
        assertEquals(120L, result.getCapaciteFoyer());

        // Verify that the repository's save method was indeed called
        verify(foyerRepository, times(1)).save(foyerToModify);
    }

    @Test
    void testRemoveFoyer() {
        // Arrange
        long foyerIdToRemove = 1L;
        // For void methods, doNothing() is good practice. It confirms the mock is set up.
        doNothing().when(foyerRepository).deleteById(foyerIdToRemove);

        // Act
        foyerService.removeFoyer(foyerIdToRemove);

        // Assert (by verifying interaction)
        // This is the most important part of testing a void method.
        // We verify that the service did its job by calling the repository's delete method.
        verify(foyerRepository, times(1)).deleteById(foyerIdToRemove);
    }
}