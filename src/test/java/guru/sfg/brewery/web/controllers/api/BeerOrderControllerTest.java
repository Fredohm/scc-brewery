package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    public void setUp() {
        super.setUp();
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

//    @DisplayName("Create Test")
//    @Nested
//    class createOrderTests {
        @Test
        void createOrderNotAuth() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isUnauthorized());
        }

        @WithUserDetails("spring")
        @Test
        void createOrderUserAdmin() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }

        @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
        @Test
        void createOrderUserAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }

        @Disabled
        @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
        @Test
        void createOrderUserNotAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

            mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isForbidden());
        }
//    }

//    @DisplayName("List Test")
//    @Nested
//    class listOrderTests {
        @WithUserDetails(value = "spring")
        @Test
        void listOrdersAdminAuth() throws Exception {
            mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                    .andExpect(status().isOk());
        }

        @WithUserDetails(value = DefaultBreweryLoader.ST_PETE_USER)
        @Test
        void listOrdersCustomerAuth() throws Exception {
            mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                    .andExpect(status().isOk());
        }

        @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
        @Test
        void listOrdersCustomerNotAuth() throws Exception {
            mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void listOrdersNotAuth() throws Exception {
            mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void listOrdersNoAuth() throws Exception {
            mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                    .andExpect(status().isUnauthorized());
        }
//    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.ST_PETE_USER)
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

//    @DisplayName("Pickup Test")
//    @Nested
//    class pickUpOrderTests {
        @Disabled
        @Test
        void pickUpOrderCustomerUserAuth() throws Exception {

        }
        @Disabled
        @Test
        void pickUpOrderCustomerUserNoAuth() throws Exception {

        }
        @Disabled
        @Test
        void pickUpOrderNotAdminUser() throws Exception {

        }
        @Disabled
        @Test
        void pickUpOrderNotAuth() throws Exception {

        }
//    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }
}
