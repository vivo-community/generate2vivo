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
@WebMvcTest(controllers = DataciteCommonsController.class)
public class DataciteCommonsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WriteResultService service;

    TestHelper helper = new TestHelper();

    @Test
    void whenValidROR_thenOrganizationReturns200() throws Exception {
        String mappingPath = "/datacitecommons/organization";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenOrganizationReturns400() throws Exception {
        String mappingPath = "/datacitecommons/organization";
        helper.invalidROR(mappingPath, mockMvc);
    }

    @Test
    void whenValidROR_thenOrganizationPlusPeopleReturns200() throws Exception {
        String mappingPath = "/datacitecommons/organizationPlusPeople";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenOrganizationPlusPeopleReturns400() throws Exception {
        String mappingPath = "/datacitecommons/organizationPlusPeople";
        helper.invalidROR(mappingPath, mockMvc);
    }

    @Test
    void whenValidROR_thenOrganizationPlusPeoplePlusPublicationsReturns200() throws Exception {
        String mappingPath = "/datacitecommons/organizationPlusPeoplePlusPublications";
        helper.validROR(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidROR_thenOrganizationPlusPeoplePlusPublicationsReturns400() throws Exception {
        String mappingPath = "/datacitecommons/organizationPlusPeoplePlusPublications";
        helper.invalidROR(mappingPath, mockMvc);
    }

    @Test
    void whenValidORCID_thenPersonReturns200() throws Exception {
        String mappingPath = "/datacitecommons/person";
        helper.validORCID(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidORCID_thenPersonReturns400() throws Exception {
        String mappingPath = "/datacitecommons/person";
        helper.invalidORCID(mappingPath, mockMvc);
    }

    @Test
    void whenValidORCID_thenPersonPlusPublicationsReturns200() throws Exception {
        String mappingPath = "/datacitecommons/personPlusPublications";
        helper.validORCID(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidORCID_thenPersonPlusPublicationsReturns400() throws Exception {
        String mappingPath = "/datacitecommons/personPlusPublications";
        helper.invalidORCID(mappingPath, mockMvc);
    }

    @Test
    void whenValidDOI_thenWorkReturns200() throws Exception {
        String mappingPath = "/datacitecommons/work";
        helper.validDOI(mappingPath, service, mockMvc);
    }

    @Test
    void whenInvalidDOI_thenWorkReturns400() throws Exception {
        String mappingPath = "/datacitecommons/work";
        helper.invalidDOI(mappingPath, mockMvc);
    }
}
