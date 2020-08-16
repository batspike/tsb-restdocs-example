package com.samcancode.web.controller;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; //DO NOT USE THIS

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcancode.domain.Beer;
import com.samcancode.repositories.BeerRepository;
import com.samcancode.web.model.BeerDto;
import com.samcancode.web.model.BeerStyleEnum;

@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "com.samcancode.web.mappers")
@AutoConfigureRestDocs(uriScheme="https", uriHost="dev.samcancode.com", uriPort=80)
@ExtendWith(RestDocumentationExtension.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerRepository beerRepository;

    @Test
    void getBeerById() throws Exception {
        given(beerRepository.findById(any())).willReturn(Optional.of(Beer.builder().build()));

        mockMvc.perform(
        				 get( "/api/v1/beer/{beerId}", UUID.randomUUID().toString() )
        				 	.param("iscold", "yes") //this is a request query param which is not needed for this endpoint; shown here for demo.
        				 	.accept(MediaType.APPLICATION_JSON)
        			   )
                .andExpect(status().isOk())
                .andDo( 
                		document("v1/beer",  //document endpoint identifier
                				  pathParameters( //document named param
                						  		  parameterWithName("beerId").description("UUID of desired beer to get.") 
                						  		),
                				  requestParameters( //document query param
                						  			 parameterWithName("iscold").description("Is Beer Cold Query param") 
                						  		   ), 
                				  responseFields( //document response field
                						  			fieldWithPath("id").description("Id of Beer"), 
                						  			fieldWithPath("version").description("Version number"), 
                						  			fieldWithPath("createdDate").description("Date Created"), 
                						  			fieldWithPath("lastModifiedDate").description("Date Updated"), 
                						  			fieldWithPath("beerName").description("Beer Name"), 
                						  			fieldWithPath("beerStyle").description("Beer Style"), 
                						  			fieldWithPath("upc").description("UPC of Beer"), 
                						  			fieldWithPath("price").description("Price"), 
                						  			fieldWithPath("quantityOnHand").description("Quantity On Hand") 
                						        )  
                	            )
                	  );
    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
        
        mockMvc.perform(post("/api/v1/beer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
                .andExpect(status().isCreated())
                .andDo( 
                		document("v1/beer",  //document endpoint identifier
                				  requestFields( //document request fields
		    						  			fields.withPath("id").ignored(), //ignored as it is not required
		    						  			fields.withPath("version").ignored(), 
		    						  			fields.withPath("createdDate").ignored(), 
		    						  			fields.withPath("lastModifiedDate").ignored(), 
		    						  			fields.withPath("beerName").description("Beer Name"), 
		    						  			fields.withPath("beerStyle").description("Beer Style"), 
		    						  			fields.withPath("upc").description("Beer UPC").attributes(), 
		    						  			fields.withPath("price").description("Beer Price"), 
		    						  			fields.withPath("quantityOnHand").ignored() 
		    						           )  
                	            )
                		);
    }

    @Test
    void updateBeerById() throws Exception {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
                .andExpect(status().isNoContent());
    }

    BeerDto getValidBeerDto(){
        return BeerDto.builder()
                .beerName("Nice Ale")
                .beerStyle(BeerStyleEnum.ALE)
                .price(new BigDecimal("9.99"))
                .upc(123123123123L)
                .build();

    }
    
    private static class ConstrainedFields {
    	private final ConstraintDescriptions constraintDescriptions;
    	
    	ConstrainedFields(Class<?> input) {
    		this.constraintDescriptions = new ConstraintDescriptions(input);
    	}
    	
    	private FieldDescriptor withPath(String path) {
    		return fieldWithPath(path)
    				.attributes(key("constraints")
    				.value(StringUtils.collectionToDelimitedString(this.constraintDescriptions.descriptionsForProperty(path), ". ")));
    	}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}