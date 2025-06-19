package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.repository.BlocRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocServiceImplTest {

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private BlocServiceImpl blocService;

    @Test
    void testRetrieveAllBlocs() {
        List<Bloc> mockBlocs = new ArrayList<>();
        mockBlocs.add(new Bloc(1L, "Bloc A", 100L, null, new HashSet<>()));
        mockBlocs.add(new Bloc(2L, "Bloc B", 150L, null, new HashSet<>()));
        when(blocRepository.findAll()).thenReturn(mockBlocs);

        List<Bloc> result = blocService.retrieveAllBlocs();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
    }

    @Test
    void testRetrieveBlocsSelonCapacite() {
        List<Bloc> allBlocs = Arrays.asList(
                new Bloc(1L, "Bloc Small", 50L, null, new HashSet<>()),
                new Bloc(2L, "Bloc Medium", 100L, null, new HashSet<>()),
                new Bloc(3L, "Bloc Large", 200L, null, new HashSet<>())
        );
        when(blocRepository.findAll()).thenReturn(allBlocs);

        long capacityThreshold = 90L;

        List<Bloc> result = blocService.retrieveBlocsSelonCapacite(capacityThreshold);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(b -> b.getNomBloc().equals("Bloc Small")));
        assertTrue(result.stream().anyMatch(b -> b.getNomBloc().equals("Bloc Medium")));
        assertTrue(result.stream().anyMatch(b -> b.getNomBloc().equals("Bloc Large")));
    }

    @Test
    void testRetrieveBloc_Success() {
        long blocId = 1L;
        Bloc mockBloc = new Bloc(blocId, "Test Bloc", 100L, null, new HashSet<>());
        when(blocRepository.findById(blocId)).thenReturn(Optional.of(mockBloc));

        Bloc result = blocService.retrieveBloc(blocId);

        assertNotNull(result);
        assertEquals(blocId, result.getIdBloc());
    }

    @Test
    void testRetrieveBloc_NotFound_ShouldThrowException() {
        long nonExistentId = 99L;
        when(blocRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> blocService.retrieveBloc(nonExistentId));
    }

    @Test
    void testAddBloc() {
        Bloc newBloc = new Bloc(0L, "New Bloc", 120L, null, new HashSet<>());
        Bloc savedBloc = new Bloc(1L, "New Bloc", 120L, null, new HashSet<>());
        when(blocRepository.save(any(Bloc.class))).thenReturn(savedBloc);

        Bloc result = blocService.addBloc(newBloc);

        assertNotNull(result);
        assertEquals(1L, result.getIdBloc());
        assertEquals("New Bloc", result.getNomBloc());
    }

    @Test
    void testModifyBloc() {
        Bloc existingBloc = new Bloc(1L, "Updated Bloc Name", 150L, null, new HashSet<>());
        when(blocRepository.save(any(Bloc.class))).thenReturn(existingBloc);

        Bloc result = blocService.modifyBloc(existingBloc);

        assertNotNull(result);
        assertEquals(1L, result.getIdBloc());
        assertEquals("Updated Bloc Name", result.getNomBloc());
    }

    @Test
    void testRemoveBloc() {
        long blocIdToRemove = 1L;
        doNothing().when(blocRepository).deleteById(blocIdToRemove);

        blocService.removeBloc(blocIdToRemove);

        verify(blocRepository, times(1)).deleteById(blocIdToRemove);
    }

    @Test
    void testTrouverBlocsSansFoyer() {
        List<Bloc> mockBlocs = List.of(new Bloc(1L, "Orphan Bloc", 100L, null, new HashSet<>()));
        when(blocRepository.findAllByFoyerIsNull()).thenReturn(mockBlocs);

        List<Bloc> result = blocService.trouverBlocsSansFoyer();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(blocRepository, times(1)).findAllByFoyerIsNull();
    }

    @Test
    void testTrouverBlocsParNomEtCap() {
        String nomBloc = "Specific Bloc";
        long capacite = 150L;
        List<Bloc> mockBlocs = List.of(new Bloc(1L, nomBloc, capacite, null, new HashSet<>()));
        when(blocRepository.findAllByNomBlocAndCapaciteBloc(nomBloc, capacite)).thenReturn(mockBlocs);

        List<Bloc> result = blocService.trouverBlocsParNomEtCap(nomBloc, capacite);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(nomBloc, result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAllByNomBlocAndCapaciteBloc(nomBloc, capacite);
    }
}
