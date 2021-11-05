package eu.tib.controller;

import eu.tib.service.MainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrcidController.class)
public class OrcidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MainService service;

    TestHelper helper = new TestHelper();

    @Test
    void whenValidORCID_thenPersonPlusWorksReturns200() throws Exception {
        String mappingPath = "/orcid/personPlusWorks";
        helper.validORCID(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidORCID_thenPersonPlusWorksReturns400() throws Exception {
        String mappingPath = "/orcid/personPlusWorks";
        helper.invalidORCID(mappingPath, mockMvc);
    }

    @Test
    void whenValidROR_thenCurrentEmployeesPlusWorksReturns200() throws Exception {
        String mappingPath = "/orcid/currentEmployeesPlusWorks";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenCurrentEmployeesPlusWorksReturns400() throws Exception {
        String mappingPath = "/orcid/currentEmployeesPlusWorks";
        helper.invalidROR(mappingPath, mockMvc);
    }
}
