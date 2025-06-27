package tn.esprit.tpfoyer.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.service.IEtudiantService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EtudiantRestControllerTest {

    @InjectMocks
    EtudiantRestController etudiantRestController;

    @Mock
    IEtudiantService etudiantService;

    Etudiant sampleEtudiant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleEtudiant = new Etudiant();
        sampleEtudiant.setIdEtudiant(1L);
        sampleEtudiant.setNomEtudiant("Ali");
        sampleEtudiant.setPrenomEtudiant("Ben Salah");
        sampleEtudiant.setCinEtudiant(12345678L);
        sampleEtudiant.setDateNaissance(new Date());
    }

    @Test
    void testGetEtudiants() {
        List<Etudiant> expectedList = Collections.singletonList(sampleEtudiant);
        when(etudiantService.retrieveAllEtudiants()).thenReturn(expectedList);

        List<Etudiant> result = etudiantRestController.getEtudiants();

        assertEquals(1, result.size());
        verify(etudiantService, times(1)).retrieveAllEtudiants();
    }

    @Test
    void testRetrieveEtudiantParCin() {
        when(etudiantService.recupererEtudiantParCin(12345678L)).thenReturn(sampleEtudiant);

        Etudiant result = etudiantRestController.retrieveEtudiantParCin(12345678L);

        assertEquals("Ali", result.getNomEtudiant());
        verify(etudiantService, times(1)).recupererEtudiantParCin(12345678L);
    }

    @Test
    void testRetrieveEtudiantById() {
        when(etudiantService.retrieveEtudiant(1L)).thenReturn(sampleEtudiant);

        Etudiant result = etudiantRestController.retrieveEtudiant(1L);

        assertEquals(1L, result.getIdEtudiant());
        verify(etudiantService, times(1)).retrieveEtudiant(1L);
    }

    @Test
    void testAddEtudiant() {
        when(etudiantService.addEtudiant(any(Etudiant.class))).thenReturn(sampleEtudiant);

        Etudiant result = etudiantRestController.addEtudiant(sampleEtudiant);

        assertNotNull(result);
        assertEquals("Ali", result.getNomEtudiant());
        verify(etudiantService, times(1)).addEtudiant(sampleEtudiant);
    }

    @Test
    void testRemoveEtudiant() {
        doNothing().when(etudiantService).removeEtudiant(1L);

        etudiantRestController.removeEtudiant(1L);

        verify(etudiantService, times(1)).removeEtudiant(1L);
    }

    @Test
    void testModifyEtudiant() {
        sampleEtudiant.setNomEtudiant("Omar");
        when(etudiantService.modifyEtudiant(any(Etudiant.class))).thenReturn(sampleEtudiant);

        Etudiant result = etudiantRestController.modifyEtudiant(sampleEtudiant);

        assertEquals("Omar", result.getNomEtudiant());
        verify(etudiantService, times(1)).modifyEtudiant(sampleEtudiant);
    }
}
