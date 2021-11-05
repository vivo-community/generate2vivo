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
@WebMvcTest(controllers = CrossrefController.class)
public class CrossrefControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MainService service;

    TestHelper helper = new TestHelper();

    @Test
    void whenValidORCID_thenPersonPlusWorksReturns200() throws Exception {
        String mappingPath = "/crossref/personPlusWorks";
        helper.validORCID(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidORCID_thenPersonPlusWorksReturns400() throws Exception {
        String mappingPath = "/crossref/personPlusWorks";
        helper.invalidORCID(mappingPath, mockMvc);
    }

    @Test
    void whenValidDOI_thenWorkReturns200() throws Exception {
        String mappingPath = "/crossref/work";
        helper.validDOI(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidDOI_thenWorkReturns400() throws Exception {
        String mappingPath = "/crossref/work";
        helper.invalidDOI(mappingPath, mockMvc);
    }
}
