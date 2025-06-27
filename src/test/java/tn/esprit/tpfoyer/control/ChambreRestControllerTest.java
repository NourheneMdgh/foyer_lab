package tn.esprit.tpfoyer.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.service.IChambreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChambreRestController.class)
class ChambreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IChambreService chambreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetChambres() throws Exception {
        Chambre c = new Chambre();
        c.setIdChambre(1L);
        c.setNumeroChambre(101L);
        c.setTypeC(TypeChambre.SIMPLE);

        Mockito.when(chambreService.retrieveAllChambres()).thenReturn(List.of(c));

        mockMvc.perform(get("/chambre/retrieve-all-chambres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroChambre").value(101));
    }

    @Test
    void testRetrieveChambre() throws Exception {
        Chambre c = new Chambre();
        c.setIdChambre(2L);
        c.setNumeroChambre(102L);
        c.setTypeC(TypeChambre.DOUBLE);

        Mockito.when(chambreService.retrieveChambre(2L)).thenReturn(c);

        mockMvc.perform(get("/chambre/retrieve-chambre/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroChambre").value(102));
    }

    @Test
    void testAddChambre() throws Exception {
        Chambre input = new Chambre();
        input.setNumeroChambre(103L);
        input.setTypeC(TypeChambre.TRIPLE);

        Chambre saved = new Chambre();
        saved.setIdChambre(3L);
        saved.setNumeroChambre(103L);
        saved.setTypeC(TypeChambre.TRIPLE);

        Mockito.when(chambreService.addChambre(any(Chambre.class))).thenReturn(saved);

        mockMvc.perform(post("/chambre/add-chambre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idChambre").value(3));
    }

    @Test
    void testModifyChambre() throws Exception {
        Chambre updated = new Chambre();
        updated.setIdChambre(4L);
        updated.setNumeroChambre(104L);
        updated.setTypeC(TypeChambre.DOUBLE);

        Mockito.when(chambreService.modifyChambre(any(Chambre.class))).thenReturn(updated);

        mockMvc.perform(put("/chambre/modify-chambre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroChambre").value(104));
    }

    @Test
    void testRemoveChambre() throws Exception {
        mockMvc.perform(delete("/chambre/remove-chambre/5"))
                .andExpect(status().isOk());

        Mockito.verify(chambreService).removeChambre(5L);
    }

    @Test
    void testTrouverChSelonTC() throws Exception {
        Chambre c = new Chambre();
        c.setIdChambre(6L);
        c.setNumeroChambre(105L);
        c.setTypeC(TypeChambre.SIMPLE);

        Mockito.when(chambreService.recupererChambresSelonTyp(TypeChambre.SIMPLE)).thenReturn(List.of(c));

        mockMvc.perform(get("/chambre/trouver-chambres-selon-typ/SIMPLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeC").value("SIMPLE"));
    }

    @Test
    void testTrouverChSelonEt() throws Exception {
        Chambre c = new Chambre();
        c.setIdChambre(7L);
        c.setNumeroChambre(106L);
        c.setTypeC(TypeChambre.DOUBLE);

        Mockito.when(chambreService.trouverchambreSelonEtudiant(12345678L)).thenReturn(c);

        mockMvc.perform(get("/chambre/trouver-chambre-selon-etudiant/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroChambre").value(106));
    }
}
