package eu.tib.controller;

import eu.tib.service.WriteResultService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RORController.class)
public class RORControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WriteResultService service;

    TestHelper helper = new TestHelper();

    @Test
    void whenValidROR_thenOrganizationReturns200() throws Exception {
        String mappingPath = "/ror/organization";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenOrganizationReturns400() throws Exception {
        String mappingPath = "/ror/organization";
        helper.invalidROR(mappingPath, mockMvc);
    }

    @Test
    void whenValidROR_thenOrganizationPlusChildrenReturns200() throws Exception {
        String mappingPath = "/ror/organizationPlusChildren";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenOrganizationPlusChildrenReturns400() throws Exception {
        String mappingPath = "/ror/organizationPlusChildren";
        helper.invalidROR(mappingPath, mockMvc);
    }
}
