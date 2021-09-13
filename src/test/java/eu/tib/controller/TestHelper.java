package eu.tib.controller;

import eu.tib.service.ResponseService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestHelper {

    private final String ror = "ror";
    private final String validROR = "https://ror.org/04aj4c181";
    private final String invalidROR = "xyz";
    private final String orcid = "orcid";
    private final String validORCID = "https://orcid.org/0000-0002-8913-9011";
    private final String invalidORCID = "xyz";
    private final String doi = "doi";
    private final String validDOI = "10.5281/zenodo.5027304";
    private final String invalidDOI = "xyz";

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Map<String, String>> paramCaptor = ArgumentCaptor.forClass(Map.class);

    public void validROR(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(ror, validROR))
                .andExpect(status().isOk());

        verify(service).buildResponse(idCaptor.capture(), paramCaptor.capture());
        assertEquals(mappingPath2resPath(mappingPath), idCaptor.getValue());
        assertEquals(Collections.singletonMap(ror,validROR), paramCaptor.getValue());
    }

    public void invalidROR(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(ror, invalidROR))
                .andExpect(status().isBadRequest());
    }

    public void validORCID(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(orcid, validORCID))
                .andExpect(status().isOk());

        verify(service).buildResponse(idCaptor.capture(), paramCaptor.capture());
        assertEquals(mappingPath2resPath(mappingPath), idCaptor.getValue());
        assertEquals(Collections.singletonMap(orcid,validORCID), paramCaptor.getValue());
    }

    public void invalidORCID(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(orcid, invalidORCID))
                .andExpect(status().isBadRequest());
    }

    public void validDOI(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(doi, validDOI))
                .andExpect(status().isOk());

        verify(service).buildResponse(idCaptor.capture(), paramCaptor.capture());
        assertEquals(mappingPath2resPath(mappingPath), idCaptor.getValue());
        assertEquals(Collections.singletonMap(doi,validDOI), paramCaptor.getValue());
    }

    public void invalidDOI(String mappingPath, ResponseService service, MockMvc mockMvc) throws Exception {
        mockMvc.perform(get(mappingPath)
                        .param(doi, invalidDOI))
                .andExpect(status().isBadRequest());
    }

    private String mappingPath2resPath(String mappingPath) {
        return "sparqlg" + mappingPath;
    }
}
