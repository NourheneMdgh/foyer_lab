package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.repository.EtudiantRepository;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @Test
    void testRetrieveAllEtudiants() {
        // Arrange
        Etudiant etudiant1 = new Etudiant(1L, "John", "Doe", 12345678L, "ecole", new Date(), null);
        Etudiant etudiant2 = new Etudiant(2L, "Jane", "Smith", 87654321L, "ecole", new Date(), null);
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant1, etudiant2));

        // Act
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testRetrieveEtudiant_Success() {
        // Arrange
        long etudiantId = 1L;
        Etudiant mockEtudiant = new Etudiant(etudiantId, "John", "Doe", 12345678L, "ecole", new Date(), null);
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(mockEtudiant));

        // Act
        Etudiant result = etudiantService.retrieveEtudiant(etudiantId);

        // Assert
        assertNotNull(result);
        assertEquals(etudiantId, result.getIdEtudiant());
        assertEquals("John", result.getNomEtudiant());
    }

    @Test
    void testRetrieveEtudiant_NotFound_ShouldThrowException() {
        // Arrange
        long nonExistentId = 99L;
        when(etudiantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        // This is a critical test. It ensures your service behaves predictably when an ID doesn't exist.
        // Calling .get() on an empty Optional should throw this exception.
        assertThrows(NoSuchElementException.class, () -> {
            etudiantService.retrieveEtudiant(nonExistentId);
        });
    }

    @Test
    void testAddEtudiant() {
        // Arrange
        Etudiant newEtudiant = new Etudiant(null, "New", "Student", 11223344L, "ecole", new Date(), null);
        Etudiant savedEtudiant = new Etudiant(5L, "New", "Student", 11223344L, "ecole", new Date(), null);
        // We use any(Etudiant.class) because the object passed to save will not be the exact same instance.
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(savedEtudiant);

        // Act
        Etudiant result = etudiantService.addEtudiant(newEtudiant);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getIdEtudiant()); // Verify that the ID from the saved object is returned
    }

    @Test
    void testModifyEtudiant() {
        // Arrange
        Etudiant existingEtudiant = new Etudiant(1L, "Updated", "Name", 12345678L, "ecole", new Date(), null);
        when(etudiantRepository.save(existingEtudiant)).thenReturn(existingEtudiant);

        // Act
        Etudiant result = etudiantService.modifyEtudiant(existingEtudiant);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getNomEtudiant());
        verify(etudiantRepository, times(1)).save(existingEtudiant); // Verify the save method was called
    }

    @Test
    void testRemoveEtudiant() {
        // Arrange
        long etudiantIdToRemove = 1L;
        doNothing().when(etudiantRepository).deleteById(etudiantIdToRemove);

        // Act
        etudiantService.removeEtudiant(etudiantIdToRemove);

        // Assert (Verify Interaction)
        // For a void method, the most important assertion is verifying that the correct
        // dependency method was called with the correct parameters.
        verify(etudiantRepository, times(1)).deleteById(etudiantIdToRemove);
    }

    @Test
    void testRecupererEtudiantParCin() {
        // Arrange
        long cin = 12345678L;
        Etudiant mockEtudiant = new Etudiant(1L, "John", "Doe", cin, "ecole", new Date(), null);
        when(etudiantRepository.findEtudiantByCinEtudiant(cin)).thenReturn(mockEtudiant);

        // Act
        Etudiant result = etudiantService.recupererEtudiantParCin(cin);

        // Assert
        assertNotNull(result);
        assertEquals(cin, result.getCinEtudiant());
        verify(etudiantRepository, times(1)).findEtudiantByCinEtudiant(cin);
    }
}