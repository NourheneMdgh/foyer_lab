package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.repository.ChambreRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChambreServiceImplTest {

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ChambreServiceImpl chambreService;

    @Test
    void testRetrieveAllChambres() {
        Set<Reservation> emptyReservations = new HashSet<>();
        List<Chambre> mockList = List.of(
            new Chambre(1L, 101L, TypeChambre.SIMPLE, emptyReservations, null),
            new Chambre(2L, 102L, TypeChambre.DOUBLE, emptyReservations, null)
        );
        when(chambreRepository.findAll()).thenReturn(mockList);

        List<Chambre> result = chambreService.retrieveAllChambres();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getNumeroChambre());
    }

    @Test
    void testRetrieveChambre_Success() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Chambre mockCh = new Chambre(3L, 103L, TypeChambre.SIMPLE, emptyReservations, null);
        when(chambreRepository.findById(3L)).thenReturn(Optional.of(mockCh));

        Chambre result = chambreService.retrieveChambre(3L);

        assertNotNull(result);
        assertEquals(103L, result.getNumeroChambre());
    }

    @Test
    void testRetrieveChambre_NotFound() {
        when(chambreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> chambreService.retrieveChambre(99L));
    }

    @Test
    void testAddChambre() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Chambre newCh = new Chambre(0L, 104L, TypeChambre.DOUBLE, emptyReservations, null);
        Chambre savedCh = new Chambre(4L, 104L, TypeChambre.DOUBLE, emptyReservations, null);
        when(chambreRepository.save(any(Chambre.class))).thenReturn(savedCh);

        Chambre result = chambreService.addChambre(newCh);

        assertNotNull(result);
        assertEquals(4L, result.getIdChambre());
        assertEquals(104L, result.getNumeroChambre());
    }

    @Test
    void testModifyChambre() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Chambre existing = new Chambre(5L, 105L, TypeChambre.SIMPLE, emptyReservations, null);
        when(chambreRepository.save(any(Chambre.class))).thenReturn(existing);

        Chambre result = chambreService.modifyChambre(existing);

        assertNotNull(result);
        assertEquals(105L, result.getNumeroChambre());
    }

    @Test
    void testRemoveChambre() {
        doNothing().when(chambreRepository).deleteById(6L);

        chambreService.removeChambre(6L);

        verify(chambreRepository, times(1)).deleteById(6L);
    }

    @Test
    void testRecupererChambresSelonType() {
        Set<Reservation> emptyReservations = new HashSet<>();
        List<Chambre> list = List.of(
            new Chambre(7L, 107L, TypeChambre.SIMPLE, emptyReservations, null)
        );
        when(chambreRepository.findAllByTypeC(TypeChambre.SIMPLE)).thenReturn(list);

        List<Chambre> result = chambreService.recupererChambresSelonTyp(TypeChambre.SIMPLE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(chambreRepository, times(1)).findAllByTypeC(TypeChambre.SIMPLE);
    }

    @Test
    void testTrouverChambreSelonEtudiant() {
        Set<Reservation> emptyReservations = new HashSet<>();
        Chambre c = new Chambre(8L, 108L, TypeChambre.DOUBLE, emptyReservations, null);
        when(chambreRepository.trouverChselonEt(123456L)).thenReturn(c);

        Chambre result = chambreService.trouverchambreSelonEtudiant(123456L);

        assertNotNull(result);
        assertEquals(108L, result.getNumeroChambre());
    }
}
