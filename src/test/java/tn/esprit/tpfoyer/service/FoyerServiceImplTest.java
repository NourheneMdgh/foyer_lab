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

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private FoyerServiceImpl foyerService;

    @Test
    void testRetrieveAllFoyers() {
        Foyer foyer1 = new Foyer(1L, "Foyer El Ghazela", 100L);
        Foyer foyer2 = new Foyer(2L, "Foyer Charguia", 150L);
        when(foyerRepository.findAll()).thenReturn(List.of(foyer1, foyer2));

        List<Foyer> result = foyerService.retrieveAllFoyers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Foyer El Ghazela", result.get(0).getNomFoyer());
    }

    @Test
    void testRetrieveFoyer_Success() {
        long foyerId = 1L;
        Foyer mockFoyer = new Foyer(foyerId, "Foyer Test", 100L);
        when(foyerRepository.findById(foyerId)).thenReturn(Optional.of(mockFoyer));

        Foyer result = foyerService.retrieveFoyer(foyerId);

        assertNotNull(result);
        assertEquals(foyerId, result.getIdFoyer());
    }

    @Test
    void testRetrieveFoyer_NotFound_ShouldThrowException() {
        long nonExistentId = 99L;
        when(foyerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            foyerService.retrieveFoyer(nonExistentId);
        }, "An exception should be thrown when the foyer ID does not exist.");
    }

    @Test
    void testAddFoyer() {
        Foyer foyerToSave = new Foyer(null, "New Foyer", 200L);
        Foyer savedFoyer = new Foyer(10L, "New Foyer", 200L);
        when(foyerRepository.save(any(Foyer.class))).thenReturn(savedFoyer);

        Foyer result = foyerService.addFoyer(foyerToSave);

        assertNotNull(result);
        assertEquals(10L, result.getIdFoyer());
    }

    @Test
    void testModifyFoyer() {
        Foyer foyerToModify = new Foyer(1L, "Updated Foyer Name", 120L);
        when(foyerRepository.save(foyerToModify)).thenReturn(foyerToModify);

        Foyer result = foyerService.modifyFoyer(foyerToModify);

        assertNotNull(result);
        assertEquals("Updated Foyer Name", result.getNomFoyer());
        assertEquals(120L, result.getCapaciteFoyer());

        verify(foyerRepository, times(1)).save(foyerToModify);
    }

    @Test
    void testRemoveFoyer() {
        long foyerIdToRemove = 1L;
        doNothing().when(foyerRepository).deleteById(foyerIdToRemove);

        foyerService.removeFoyer(foyerIdToRemove);

        verify(foyerRepository, times(1)).deleteById(foyerIdToRemove);
    }
}
