package com.training.ecommerce.services;

import com.training.ecommerce.entities.Product;
import com.training.ecommerce.exceptions.ProductException;
import com.training.ecommerce.repositories.ProductRepository;
import com.training.ecommerce.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductUtils productUtils;

    public Product addProduct(Product product)throws RuntimeException{
        if(productRepo.existsByCode(product.getCode())){
            throw new ProductException("Il prodotto " + product.getName() + " è già esistente", HttpStatus.CONFLICT);
        }
        String prefix = product.getCode(); // il client manda solo "EL"

        // Trova l'ultimo codice che inizia con questo prefisso
        String lastCode = productRepo.findTopByCodeStartingWithOrderByCodeDesc(prefix)
                .map(Product::getCode)
                .orElse(prefix + "000"); // se non ci sono ancora prodotti con questo prefisso

        // Genera il nuovo codice incrementando la parte numerica
        String newCode = incrementProductCode(lastCode);

        product.setCode(newCode);
        return productRepo.save(product);
    }


    public Product modifyProduct(String code, Product newData){
        Product oldData = productUtils.findProductByCode(code);
        BeanUtils.copyProperties(newData, oldData, "id", "code");
        return productRepo.save(oldData);
    }

    private String incrementProductCode(String code) {
//      \\d → un singolo carattere numerico (0-9)
//      .* → qualsiasi cosa che segue quel numero, fino alla fine della stringa
//      Quindi (\\d.*) cattura tutto dalla prima cifra numerica fino alla fine.
        String prefix = code.replaceAll("(\\d.*)", "");

//      \\D → qualsiasi carattere non numerico
//      + → uno o più caratteri consecutivi
//      replaceAll("\\D+", "") → rimuove tutto ciò che non è numero, lasciando solo la parte numerica.
        String numberPart = code.replaceAll("\\D+", "");

        int number = Integer.parseInt(numberPart) + 1;

        // Mantieni la stessa lunghezza della parte numerica originale
//      %d → formato decimale
//      0 → indica che eventuali spazi vuoti devono essere riempiti con zeri
//      numberPart.length() → specifica la lunghezza totale della stringa numerica
        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);

        return prefix + newNumberPart;
    }



    public Product findProduct(String code){
        return productUtils.findProductByCode(code);
    }

    public List<Product> findAllProducts(Pageable pageable, String searchFilter){
            return (searchFilter==null) ? productRepo.findAll(pageable).getContent()
                : productRepo.search(searchFilter, pageable).getContent();
    }


    public void deleteProduct(String code){
        Product product = productUtils.findProductByCode(code);
        productRepo.delete(product);
    }
}
