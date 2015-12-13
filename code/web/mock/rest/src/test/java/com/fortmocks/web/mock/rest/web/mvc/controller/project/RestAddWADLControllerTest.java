/*
 * Copyright 2015 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortmocks.web.mock.rest.web.mvc.controller.project;

import com.fortmocks.core.basis.model.ServiceProcessor;
import com.fortmocks.core.mock.rest.model.project.dto.RestApplicationDto;
import com.fortmocks.core.mock.rest.model.project.dto.RestProjectDto;
import com.fortmocks.core.mock.rest.model.project.service.message.input.ReadRestProjectInput;
import com.fortmocks.core.mock.rest.model.project.service.message.output.ReadRestProjectOutput;
import com.fortmocks.web.basis.web.mvc.controller.AbstractController;
import com.fortmocks.web.mock.rest.config.TestApplication;
import com.fortmocks.web.mock.rest.manager.WADLComponent;
import com.fortmocks.web.mock.rest.model.project.RestApplicationDtoGenerator;
import com.fortmocks.web.mock.rest.model.project.RestProjectDtoGenerator;
import com.fortmocks.web.mock.rest.web.mvc.command.project.WADLFileUploadForm;
import com.fortmocks.web.mock.rest.web.mvc.controller.AbstractRestControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author Karl Dahlgren
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class RestAddWADLControllerTest extends AbstractRestControllerTest {

    private static final String PAGE = "partial/mock/rest/project/restAddWADL.jsp";
    private static final String ADD = "add";
    private static final String WADL = "wadl";
    private static final int MAX_SOAP_PORT_COUNT = 5;

    @InjectMocks
    private RestAddWADLController  restAddWADLController;

    @Mock
    private ServiceProcessor serviceProcessor;

    @Mock
    private WADLComponent wadlComponent;

    @Override
    protected AbstractController getController() {
        return restAddWADLController;
    }

    @Test
    public void testAddWADLGet() throws Exception {
        final RestProjectDto restProjectDto = RestProjectDtoGenerator.generateRestProjectDto();
        when(serviceProcessor.process(any(ReadRestProjectInput.class))).thenReturn(new ReadRestProjectOutput(restProjectDto));
        final MockHttpServletRequestBuilder message = MockMvcRequestBuilders.get(SERVICE_URL + SLASH + PROJECT + SLASH + restProjectDto.getId() + SLASH + ADD + SLASH + WADL);
        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(6))
                .andExpect(MockMvcResultMatchers.forwardedUrl(INDEX))
                .andExpect(MockMvcResultMatchers.model().attribute(PARTIAL, PAGE))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_PROJECT_ID, restProjectDto.getId()));
    }

    @Test
    public void testAddWADLPostFile() throws Exception {
        final RestProjectDto restProjectDto = RestProjectDtoGenerator.generateRestProjectDto();
        final List<RestApplicationDto> restApplicationDtos = new ArrayList<RestApplicationDto>();
        final WADLFileUploadForm uploadForm = new WADLFileUploadForm();
        final List<MultipartFile> uploadedFiles = new ArrayList<>();
        uploadForm.setFiles(uploadedFiles);

        for(int index = 0; index < MAX_SOAP_PORT_COUNT; index++){
            final RestApplicationDto restApplicationDto = RestApplicationDtoGenerator.generateRestApplicationDto();
            restApplicationDtos.add(restApplicationDto);
        }

        when(serviceProcessor.process(any(ReadRestProjectInput.class))).thenReturn(new ReadRestProjectOutput(restProjectDto));
        when(wadlComponent.createApplication(anyListOf(MultipartFile.class), anyBoolean())).thenReturn(restApplicationDtos);
        final MockHttpServletRequestBuilder message = MockMvcRequestBuilders.post(SERVICE_URL + SLASH + PROJECT + SLASH + restProjectDto.getId() + SLASH + ADD + SLASH + WADL).param("type", "file").requestAttr("uploadForm", uploadForm);
        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().size(1));
        Mockito.verify(wadlComponent, times(1)).createApplication(anyListOf(MultipartFile.class), anyBoolean());
    }

    @Test
    public void testAddWADLPostLink() throws Exception {
        final RestProjectDto restProjectDto = RestProjectDtoGenerator.generateRestProjectDto();
        final List<RestApplicationDto> restApplicationDtos = new ArrayList<RestApplicationDto>();
        final WADLFileUploadForm uploadForm = new WADLFileUploadForm();
        final List<MultipartFile> uploadedFiles = new ArrayList<>();
        uploadForm.setFiles(uploadedFiles);

        for(int index = 0; index < MAX_SOAP_PORT_COUNT; index++){
            final RestApplicationDto restApplicationDto = RestApplicationDtoGenerator.generateRestApplicationDto();
            restApplicationDtos.add(restApplicationDto);
        }

        when(serviceProcessor.process(any(ReadRestProjectInput.class))).thenReturn(new ReadRestProjectOutput(restProjectDto));
        when(wadlComponent.createApplication(anyListOf(MultipartFile.class), anyBoolean())).thenReturn(restApplicationDtos);
        final MockHttpServletRequestBuilder message = MockMvcRequestBuilders.post(SERVICE_URL + SLASH + PROJECT + SLASH + restProjectDto.getId() + SLASH + ADD + SLASH + WADL).param("type", "link").requestAttr("uploadForm", uploadForm);
        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().size(1));
        Mockito.verify(wadlComponent, times(1)).createApplication(anyString(), anyBoolean());
    }



}