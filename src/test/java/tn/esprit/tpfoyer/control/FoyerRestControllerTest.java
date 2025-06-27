package tn.esprit.tpfoyer.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.service.IFoyerService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FoyerRestControllerTest {

    @Mock
    IFoyerService foyerService;

    @InjectMocks
    FoyerRestController foyerRestController;

    Foyer sampleFoyer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleFoyer = new Foyer(1L, "Foyer Central", 1000);
    }

    @Test
    void testGetFoyers() {
        List<Foyer> foyerList = Collections.singletonList(sampleFoyer);
        when(foyerService.retrieveAllFoyers()).thenReturn(foyerList);

        List<Foyer> result = foyerRestController.getFoyers();

        assertEquals(1, result.size());
        assertEquals("Foyer Central", result.get(0).getNomFoyer());
        verify(foyerService, times(1)).retrieveAllFoyers();
    }

    @Test
    void testRetrieveFoyerById() {
        when(foyerService.retrieveFoyer(1L)).thenReturn(sampleFoyer);

        Foyer result = foyerRestController.retrieveFoyer(1L);

        assertEquals(1L, result.getIdFoyer());
        assertEquals("Foyer Central", result.getNomFoyer());
        verify(foyerService, times(1)).retrieveFoyer(1L);
    }

    @Test
    void testAddFoyer() {
        when(foyerService.addFoyer(sampleFoyer)).thenReturn(sampleFoyer);

        Foyer result = foyerRestController.addFoyer(sampleFoyer);

        assertNotNull(result);
        assertEquals("Foyer Central", result.getNomFoyer());
        verify(foyerService, times(1)).addFoyer(sampleFoyer);
    }

    @Test
    void testRemoveFoyer() {
        doNothing().when(foyerService).removeFoyer(1L);

        foyerRestController.removeFoyer(1L);

        verify(foyerService, times(1)).removeFoyer(1L);
    }

    @Test
    void testModifyFoyer() {
        sampleFoyer.setNomFoyer("Updated Foyer");
        when(foyerService.modifyFoyer(sampleFoyer)).thenReturn(sampleFoyer);

        Foyer result = foyerRestController.modifyFoyer(sampleFoyer);

        assertEquals("Updated Foyer", result.getNomFoyer());
        verify(foyerService, times(1)).modifyFoyer(sampleFoyer);
    }
}
