package java_spring_boot.hw7.services.impl;

import java_spring_boot.hw7.entities.*;
import java_spring_boot.hw7.repositories.*;
import java_spring_boot.hw7.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private Items_BasketRepository items_basketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Items addItem(Items item) {
        return itemsRepository.save(item);
    }

    //Items
    @Override
    public List<Items> getAllItems() {
        return itemsRepository.findByPriceGreaterThan(0);
    }

    @Override
    public Items getItem(Long id) {
        return itemsRepository.getOne(id);
    }

    @Override
    public void deleteItem(Items item) {
        itemsRepository.delete(item);
    }

    @Override
    public Items saveItem(Items item) {
        return itemsRepository.save(item);
    }

    @Override
    public List<Items> getAllTop() {
        return itemsRepository.findAll(Sort.by("inTopPage").descending());
    }

    @Override
    public List<Items> findAllByNameContainingOrderByPriceAsc(String name) {
        return itemsRepository.findAllByNameContainingOrderByPriceAsc(name);
    }

    @Override
    public List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceDesc(double price1, double price2, String name) {
        return itemsRepository.findAllByPriceBetweenAndNameContainingOrderByPriceDesc(price1,price2,name);
    }

    @Override
    public List<Items> findAllByPriceBetweenAndNameContainingOrderByPriceAsc(double price1, double price2, String name) {
        return itemsRepository.findAllByPriceBetweenAndNameContainingOrderByPriceAsc(price1,price2,name);
    }

    //Countries
    @Override
    public Countries addCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public void deleteCountry(Countries country) {
        countryRepository.delete(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countryRepository.save(country);
    }

    //Brands
    @Override
    public Brands addBrand(Brands brand) {
        return brandRepository.save(brand);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public void deleteBrand(Brands brand) {
        brandRepository.delete(brand);
    }

    @Override
    public Brands saveBrand(Brands brand) {
        return brandRepository.save(brand);
    }

    @Override
    public List<Items> findAllByBrandAndNameOrderByPriceDesc(double price1, double price2, String name,Brands brand_id) {
        return itemsRepository.findAllByPriceBetweenAndNameContainingAndBrandOrderByPriceDesc(price1,price2,name,brand_id);
    }

    @Override
    public List<Items> findAllByBrandOrderByPriceAsc(Brands brands_id) {
        return itemsRepository.findAllByBrandOrderByPriceAsc(brands_id);
    }

    @Override
    public List<Items> findAllByBrandOrderByPriceDesc(Brands brands_id) {
        return itemsRepository.findAllByBrandOrderByPriceDesc(brands_id);
    }

    @Override
    public List<Items> findAllByBrandAndNameOrderByPriceAsc(double price1, double price2, String name,Brands brands) {
        return itemsRepository.findAllByPriceBetweenAndNameContainingAndBrandOrderByPriceAsc(price1,price2,name,brands);
    }

    //Categories
    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories addCategory(Categories categories) {
        return categoryRepository.save(categories);
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Categories saveCategory(Categories categories) {
        return categoryRepository.save(categories);
    }

    @Override
    public void deleteCategory(Categories categories) {
        categoryRepository.delete(categories);
    }

    @Override
    public List<Items_Basket> getAllItems_baskets() {
        return  items_basketRepository.findAll();
    }

    @Override
    public Items_Basket saveItems_Basket(Items_Basket items_basket) {
        return items_basketRepository.save(items_basket);
    }
    @Override
    public List<Pictures> getAllPictures() {
        return pictureRepository.findAll();
    }

    @Override
    public Pictures addPicture(Pictures picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public Pictures getPicture(Long id) {
        return pictureRepository.getOne(id);
    }

    @Override
    public void deletePictires(Pictures pictures) {
        pictureRepository.delete(pictures);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}
