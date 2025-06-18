package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.repository.BlocRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceImplTest {

    @Mock
    private BlocRepository blocRepository; // We create a fake repository

    @InjectMocks
    private BlocServiceImpl blocService; // This creates a real service and injects the fake repository

    // --- Test for retrieveAllBlocs ---
    // Note: We are testing the LOGIC of the method, not the @Scheduled annotation.
    // The Spring framework is responsible for calling the method on schedule.
    // Our job is to ensure that WHEN it's called, it works correctly.
    @Test
    void testRetrieveAllBlocs() {
        // Arrange: Prepare the data and mock's behavior
        List<Bloc> mockBlocs = new ArrayList<>();
        mockBlocs.add(new Bloc(1L, "Bloc A", 100L, null));
        mockBlocs.add(new Bloc(2L, "Bloc B", 150L, null));
        when(blocRepository.findAll()).thenReturn(mockBlocs);

        // Act: Call the method we are testing
        List<Bloc> result = blocService.retrieveAllBlocs();

        // Assert: Check the outcome
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
    }

    // --- Test for the method with business logic ---
    @Test
    void testRetrieveBlocsSelonCapacite() {
        // Arrange
        List<Bloc> allBlocs = new ArrayList<>();
        allBlocs.add(new Bloc(1L, "Bloc Small", 50L, null));
        allBlocs.add(new Bloc(2L, "Bloc Medium", 100L, null));
        allBlocs.add(new Bloc(3L, "Bloc Large", 200L, null));
        when(blocRepository.findAll()).thenReturn(allBlocs);

        long capacityThreshold = 90L;

        // Act
        List<Bloc> result = blocService.retrieveBlocsSelonCapacite(capacityThreshold);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Should only return blocs with capacity >= 90");
        // Verify that the small bloc is not in the result
        assertTrue(result.stream().noneMatch(b -> b.getNomBloc().equals("Bloc Small")));
        // Verify that the medium and large blocs are in the result
        assertTrue(result.stream().anyMatch(b -> b.getNomBloc().equals("Bloc Medium")));
        assertTrue(result.stream().anyMatch(b -> b.getNomBloc().equals("Bloc Large")));
    }


    @Test
    void testRetrieveBloc_Success() {
        // Arrange
        long blocId = 1L;
        Bloc mockBloc = new Bloc(blocId, "Test Bloc", 100L, null);
        when(blocRepository.findById(blocId)).thenReturn(Optional.of(mockBloc));

        // Act
        Bloc result = blocService.retrieveBloc(blocId);

        // Assert
        assertNotNull(result);
        assertEquals(blocId, result.getIdBloc());
    }

    @Test
    void testRetrieveBloc_NotFound_ShouldThrowException() {
        // Arrange
        long nonExistentId = 99L;
        when(blocRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        // We assert that calling retrieveBloc with this ID will throw a NoSuchElementException
        // because your code calls .get() on an empty Optional.
        assertThrows(NoSuchElementException.class, () -> {
            blocService.retrieveBloc(nonExistentId);
        });
    }

    @Test
    void testAddBloc() {
        // Arrange
        Bloc newBloc = new Bloc(null, "New Bloc", 120L, null); // ID is null before saving
        Bloc savedBloc = new Bloc(1L, "New Bloc", 120L, null); // ID is set after saving
        when(blocRepository.save(any(Bloc.class))).thenReturn(savedBloc);

        // Act
        Bloc result = blocService.addBloc(newBloc);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdBloc());
        assertEquals("New Bloc", result.getNomBloc());
    }

    @Test
    void testModifyBloc() {
        // Arrange
        Bloc existingBloc = new Bloc(1L, "Updated Bloc Name", 150L, null);
        when(blocRepository.save(any(Bloc.class))).thenReturn(existingBloc);

        // Act
        Bloc result = blocService.modifyBloc(existingBloc);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdBloc());
        assertEquals("Updated Bloc Name", result.getNomBloc());
    }

    @Test
    void testRemoveBloc() {
        // Arrange
        long blocIdToRemove = 1L;
        // For void methods, we can use doNothing(). This is often optional but good practice.
        doNothing().when(blocRepository).deleteById(blocIdToRemove);

        // Act
        blocService.removeBloc(blocIdToRemove);

        // Assert / Verify
        // The most important part for a void method is to VERIFY
        // that the repository's deleteById method was called exactly once with the correct ID.
        verify(blocRepository, times(1)).deleteById(blocIdToRemove);
    }

    @Test
    void testTrouverBlocsSansFoyer() {
        // Arrange
        List<Bloc> mockBlocs = new ArrayList<>();
        mockBlocs.add(new Bloc(1L, "Orphan Bloc", 100L, null));
        when(blocRepository.findAllByFoyerIsNull()).thenReturn(mockBlocs);

        // Act
        List<Bloc> result = blocService.trouverBlocsSansFoyer();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(blocRepository, times(1)).findAllByFoyerIsNull();
    }

    @Test
    void testTrouverBlocsParNomEtCap() {
        // Arrange
        String nomBloc = "Specific Bloc";
        long capacite = 150L;
        List<Bloc> mockBlocs = List.of(new Bloc(1L, nomBloc, capacite, null));
        when(blocRepository.findAllByNomBlocAndCapaciteBloc(nomBloc, capacite)).thenReturn(mockBlocs);

        // Act
        List<Bloc> result = blocService.trouverBlocsParNomEtCap(nomBloc, capacite);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(nomBloc, result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAllByNomBlocAndCapaciteBloc(nomBloc, capacite);
    }
}