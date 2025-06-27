package tn.esprit.tpfoyer.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.service.IBlocService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlocRestController.class)
class BlocRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBlocService blocService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetBlocs() throws Exception {
        Bloc b = new Bloc(1L, "Bloc A", 100, null, null);
        Mockito.when(blocService.retrieveAllBlocs()).thenReturn(List.of(b));

        mockMvc.perform(get("/bloc/retrieve-all-blocs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomBloc").value("Bloc A"));
    }

    @Test
    void testRetrieveBloc() throws Exception {
        Bloc b = new Bloc(1L, "Bloc B", 80, null, null);
        Mockito.when(blocService.retrieveBloc(1L)).thenReturn(b);

        mockMvc.perform(get("/bloc/retrieve-bloc/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc B"));
    }

    @Test
    void testAddBloc() throws Exception {
        Bloc inputBloc = new Bloc();
        inputBloc.setNomBloc("Bloc C");
        inputBloc.setCapaciteBloc(70);

        Bloc savedBloc = new Bloc();
        savedBloc.setIdBloc(2L);  // Simulate DB-generated ID
        savedBloc.setNomBloc("Bloc C");
        savedBloc.setCapaciteBloc(70);

        Mockito.when(blocService.addBloc(any(Bloc.class))).thenReturn(savedBloc);

        mockMvc.perform(post("/bloc/add-bloc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBloc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBloc").value(2));
    }


    @Test
    void testModifyBloc() throws Exception {
        Bloc b = new Bloc(3L, "Bloc Updated", 90, null, null);
        Mockito.when(blocService.modifyBloc(any(Bloc.class))).thenReturn(b);

        mockMvc.perform(put("/bloc/modify-bloc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomBloc").value("Bloc Updated"));
    }

    @Test
    void testRemoveBloc() throws Exception {
        mockMvc.perform(delete("/bloc/remove-bloc/3"))
                .andExpect(status().isOk());

        Mockito.verify(blocService).removeBloc(3L);
    }

    @Test
    void testGetBlocsWithoutFoyer() throws Exception {
        Bloc b = new Bloc(4L, "Orphan Bloc", 60, null, null);
        Mockito.when(blocService.trouverBlocsSansFoyer()).thenReturn(List.of(b));

        mockMvc.perform(get("/bloc/trouver-blocs-sans-foyer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomBloc").value("Orphan Bloc"));
    }

    @Test
    void testRecuperBlocsParNomEtCap() throws Exception {
        Bloc b = new Bloc(5L, "Bloc D", 120, null, null);
        Mockito.when(blocService.trouverBlocsParNomEtCap(eq("Bloc D"), eq(120L)))
                .thenReturn(List.of(b));

        mockMvc.perform(get("/bloc/get-bloc-nb-c/Bloc D/120"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomBloc").value("Bloc D"));
    }
}
