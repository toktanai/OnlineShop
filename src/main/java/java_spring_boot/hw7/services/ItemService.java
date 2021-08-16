package java_spring_boot.hw7.services;

import java_spring_boot.hw7.entities.*;

import java.util.List;


public interface ItemService {

    //Items
    Items addItem(Items item);
    List<Items> getAllItems();
    Items getItem(Long id);
    void deleteItem(Items item);
    Items saveItem(Items item);
    List<Items> getAllTop();
    List<Items> findAllByNameContainingOrderByPriceAsc(String name);
    List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceDesc(double price1, double price2, String name);
    List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceAsc(double price1, double price2, String name);


    //Countries
    Countries addCountry(Countries country);
    List<Countries> getAllCountries();
    Countries getCountry(Long id);
    void deleteCountry(Countries country);
    Countries saveCountry(Countries country);

    //Brands
    Brands addBrand(Brands brand);
    List<Brands> getAllBrands();
    Brands getBrand(Long id);
    void deleteBrand(Brands brand);
    Brands saveBrand(Brands brand);

    List<Items> findAllByBrandAndNameOrderByPriceAsc(double price1, double price2, String name,Brands brands);
    List<Items> findAllByBrandAndNameOrderByPriceDesc(double price1, double price2, String name,Brands brands);

    List<Items> findAllByBrandOrderByPriceAsc(Brands brands_id);
    List<Items> findAllByBrandOrderByPriceDesc(Brands brands_id);

    //Categories
    List<Categories> getAllCategories();
    Categories addCategory(Categories categories);
    Categories getCategory(Long id);
    Categories saveCategory(Categories categories);
    void deleteCategory(Categories categories);

    //Basket
    List<Items_Basket> getAllItems_baskets();
    Items_Basket saveItems_Basket(Items_Basket items_basket);

    //Pictures
    List<Pictures> getAllPictures();
    Pictures addPicture(Pictures picture);
    Pictures getPicture(Long id);
    void deletePictires(Pictures pictures);

    //Comment
    List<Comment> getAllComments();
    Comment addComment(Comment comment);
    Comment getComment(Long id);
    void deleteComment(Comment comment);
    Comment saveComment(Comment comment);

}
