package com.mlorenzo.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6restmvc.config.SecurityConfig;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import com.mlorenzo.spring6restmvc.services.CustomerService;
import com.mlorenzo.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerWebMvcIT {

    @Autowired
    MockMvc mockMvc;

    // Inyectamos el bean de Spring de tipo ObjectMapper con la configuración por defecto establecida por
    // Spring Boot para Jackson
    @Autowired
    ObjectMapper objectMapper;

    // Esta anotación crea un Mock de tipo BeerService y lo añade al contexto de Spring como un bean para que pueda ser
    // inyectado en el controlador BeerController
    @MockBean
    CustomerService customerService;

    // Para obtener datos de prueba
    CustomerServiceImpl customerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerDTOArgumentCaptor;

    // Se comentan porque ya no usamos Autenticación Básica
    /*@Value("${spring.security.user.name}")
    String username;*/

    /*@Value("${spring.security.user.password}")
    String password;*/

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void getListCustomersTest() throws Exception {
        given(customerService.getAll()).willReturn(customerServiceImpl.getAll());
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                        // Se comenta porque ahora esta aplicación es un servidor de recursos OAuth2 que usa tokens JWT
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerByIdTest() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getAll().get(0);
        when(customerService.getById(any(UUID.class))).thenReturn(customerDTO);
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, customerDTO.getId().toString())
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(customerDTO.getId().toString())))
                .andExpect(jsonPath("$.name", is(customerDTO.getName())));
    }

    @Test
    void getBeerByIdNotFoundTest() throws Exception {
        when(customerService.getById(any(UUID.class))).thenThrow(NotFoundException.class);
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID().toString())
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewCustomerTest() throws Exception {
        CustomerDTO savedCustomerDTO = customerServiceImpl.getAll().get(0);
        // El objeto de tipo CustomerDTO a crear va sin los campos "id", "createdDate" y "updateDate"
        CustomerDTO newCustomer = CustomerDTO.builder()
                .name(savedCustomerDTO.getName())
                .email(savedCustomerDTO.getEmail())
                .build();
        when(customerService.save(any(CustomerDTO.class))).thenReturn(savedCustomerDTO);
        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                        //.with(httpBasic(username, password))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void updateCustomerTest() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getAll().get(0);
        final UUID customerId = customerDTO.getId();
        // El objeto de tipo Customer a actualizar va sin los campos "id", "createdDate" y "updateDate"
        customerDTO.setId(null);
        customerDTO.setCreatedDate(null);
        customerDTO.setUpdateDate(null);
        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, customerId.toString())
                        //.with(httpBasic(username, password))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isNoContent());
        // Si no se indica lo contrario, por defecto verifica que se llame una vez
        verify(customerService, times(1)).update(uuidArgumentCaptor.capture(), any(CustomerDTO.class));
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerId);
    }

    @Test
    void patchCustomerTest() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAll().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        final String customerName = "customerName";
        beerMap.put("name", customerName);
        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customer.getId())
                        //.with(httpBasic(username, password))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());
        verify(customerService, times(1)).patch(uuidArgumentCaptor.capture(),
                customerDTOArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customer.getId());
        assertThat(customerDTOArgumentCaptor.getValue().getName()).isEqualTo(customerName);
    }

    @Test
    void deleteCustomerTest() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getAll().get(0);
        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customerDTO.getId().toString())
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNoContent());
        // Capturamos el parámetro que se pasa al método "deleteById" para luego verificar que es el correcto
        verify(customerService).deleteById(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDTO.getId());
    }

}