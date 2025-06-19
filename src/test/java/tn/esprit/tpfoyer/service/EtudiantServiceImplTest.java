package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.repository.EtudiantRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.NoSuchElementException;

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
        Set<Reservation> emptyReservations = new HashSet<>();
        Etudiant etudiant1 = new Etudiant(1L, "John", "Doe", 12345678L, new Date(), emptyReservations);
        Etudiant etudiant2 = new Etudiant(2L, "Jane", "Smith", 87654321L, new Date(), emptyReservations);
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant1, etudiant2));

        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testRetrieveEtudiant_Success() {
        long etudiantId = 1L;
        Set<Reservation> emptyReservations = new HashSet<>();
        Etudiant mockEtudiant = new Etudiant(etudiantId, "John", "Doe", 12345678L, new Date(), emptyReservations);
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(mockEtudiant));

        Etudiant result = etudiantService.retrieveEtudiant(etudiantId);

        assertNotNull(result);
        assertEquals(etudiantId, result.getIdEtudiant());
        assertEquals("John", result.getNomEtudiant());
    }

    @Test
    void testRetrieveEtudiant_NotFound_ShouldThrowException() {
        long nonExistentId = 99L;
        when(etudiantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            etudiantService.retrieveEtudiant(nonExistentId);
        });
    }

    @Test
    void testAddEtudiant() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Etudiant newEtudiant = new Etudiant(0L, "New", "Student", 11223344L, new Date(), emptyReservations);
        Etudiant savedEtudiant = new Etudiant(5L, "New", "Student", 11223344L, new Date(), emptyReservations);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(savedEtudiant);

        Etudiant result = etudiantService.addEtudiant(newEtudiant);

        assertNotNull(result);
        assertEquals(5L, result.getIdEtudiant());
    }

    @Test
    void testModifyEtudiant() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Etudiant existingEtudiant = new Etudiant(1L, "Updated", "Name", 12345678L, new Date(), emptyReservations);
        when(etudiantRepository.save(existingEtudiant)).thenReturn(existingEtudiant);

        Etudiant result = etudiantService.modifyEtudiant(existingEtudiant);

        assertNotNull(result);
        assertEquals("Updated", result.getNomEtudiant());
        verify(etudiantRepository, times(1)).save(existingEtudiant);
    }

    @Test
    void testRemoveEtudiant() {
        long etudiantIdToRemove = 1L;
        doNothing().when(etudiantRepository).deleteById(etudiantIdToRemove);

        etudiantService.removeEtudiant(etudiantIdToRemove);

        verify(etudiantRepository, times(1)).deleteById(etudiantIdToRemove);
    }

    @Test
    void testRecupererEtudiantParCin() {
        long cin = 12345678L;
        Set<Reservation> emptyReservations = new HashSet<>();
        Etudiant mockEtudiant = new Etudiant(1L, "John", "Doe", cin, new Date(), emptyReservations);
        when(etudiantRepository.findEtudiantByCinEtudiant(cin)).thenReturn(mockEtudiant);

        Etudiant result = etudiantService.recupererEtudiantParCin(cin);

        assertNotNull(result);
        assertEquals(cin, result.getCinEtudiant());
        verify(etudiantRepository, times(1)).findEtudiantByCinEtudiant(cin);
    }
}
