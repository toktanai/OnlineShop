package java_spring_boot.hw7.controllers;

import java_spring_boot.hw7.entities.*;
import java_spring_boot.hw7.services.ItemService;
import java_spring_boot.hw7.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.query.criteria.internal.expression.function.CurrentTimeFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Value("static/item_pictures/")
    private String viewPath_pictures;

    @Value("target/classes/static/item_pictures/")
    private String uploadPath_pictures;

    @GetMapping(value = "/")
    public String index(Model model){

        List<Items> items = itemService.getAllTop();
        model.addAttribute("items",items);


        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("currentUser", getUserData());
        return "index";

    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String admin(Model model){

        List<Items> items = itemService.getAllTop();
        model.addAttribute("items",items);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("currentUser", getUserData());
        return "admin";

    }
    @GetMapping(value = "/admin_country")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminCountry(Model model){
        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("currentUser", getUserData());
        return "admin_country";
    }

    @GetMapping(value = "/admin_brand")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminBrand(Model model){
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("currentUser", getUserData());
        return "admin_brand";
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addItem(@RequestParam(name = "name")String name,
                          @RequestParam(name = "description")String description,
                          @RequestParam(name = "price")int price,
                          @RequestParam(name = "stars")int stars,
                          @RequestParam(name = "small_url")String small_url,
                          @RequestParam(name = "large_url")String large_url,
                          @RequestParam(name = "date") Date date,
                          @RequestParam(name = "inTopPage") boolean inTopPage,
                          @RequestParam(name = "brand_id")Long id){

        Brands brn = itemService.getBrand(id);
        if (brn != null){
            Items item = new Items();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(stars);
            item.setSmallPicURL(small_url);
            item.setLargePicURL(large_url);
            item.setAddedDate(date);
            item.setInTopPage(inTopPage);
            item.setBrand(brn);
            itemService.addItem(item);
        }

        return "redirect:/";
    }
    @PostMapping(value = "/add_brand")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addBrand(@RequestParam(name = "name")String name,
                           @RequestParam(name = "country_id")Long id){

        Countries cnt = itemService.getCountry(id);
        if (cnt != null){
            Brands brand = new Brands();
            brand.setName(name);
            brand.setCountry(cnt);
            itemService.addBrand(brand);
        }

        return "redirect:/admin_brand";
    }

    @PostMapping(value = "/edit_brand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String edit_brand(@RequestParam(name = "id")Long id,
                           @RequestParam(name = "name")String name,
                           @RequestParam(name = "country_id")Long country_id){

        Brands brand = itemService.getBrand(id);
        if(brand!=null){
            Countries ctr = itemService.getCountry(country_id);
            brand.setName(name);
            brand.setCountry(ctr);
            itemService.saveBrand(brand);

        }
        return "redirect:/admin_brand";
    }

    @PostMapping(value = "/add_country")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCountry(@RequestParam(name = "name")String name,
                             @RequestParam(name = "code")String code){

        Countries country = itemService.addCountry(new Countries(null,name,code));


        return "redirect:/admin_country";
    }

    @GetMapping(value = "/details")
    public String details(Model model, HttpServletRequest request, @RequestParam(name = "id") Long id){

        Items item = itemService.getItem(id);
        model.addAttribute("item", item);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("currentUser", getUserData());

        List<Pictures> pictures = itemService.getAllPictures();
        pictures = pictures.stream().filter(p -> p.getItem().getId() == item.getId()).collect(Collectors.toList());

        HttpSession session = request.getSession();
        List<Items_Basket> items_basket = (List<Items_Basket>) session.getAttribute("basket");
        int amount_sum = 0;
        if(items_basket != null) {
            for (Items_Basket i : items_basket) {
                amount_sum = amount_sum + i.getAmount();
            }
        }
        model.addAttribute("amount",amount_sum);
        model.addAttribute("pictures", pictures);

        List<Comment> comments = itemService.getAllComments();
        model.addAttribute("comments", comments);

        return "details";
    }

    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String saveItem(@RequestParam(name = "id")Long id,
                          @RequestParam(name = "name")String name,
                          @RequestParam(name = "description")String description,
                          @RequestParam(name = "price")double price,
                          @RequestParam(name = "stars")int stars,
                          @RequestParam(name = "small_url")String small_url,
                          @RequestParam(name = "large_url")String large_url,
                          @RequestParam(name = "date") Date date,
                          @RequestParam(name = "inTopPage") boolean inTopPage,
                          @RequestParam(name = "brand_id")Long brand_id){

        Items item = itemService.getItem(id);
        if(item!=null){
            Brands brn = itemService.getBrand(brand_id);
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStars(stars);
            item.setSmallPicURL(small_url);
            item.setLargePicURL(large_url);
            item.setAddedDate(date);
            item.setInTopPage(inTopPage);
            item.setBrand(brn);
            itemService.saveItem(item);

        }
        return "redirect:/";
    }

    @PostMapping(value = "/save_country")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveItem(@RequestParam(name = "id")Long id,
                           @RequestParam(name = "name")String name,
                           @RequestParam(name = "code")String code){
        Countries country = itemService.getCountry(id);
        if(country!=null){
            country.setName(name);
            country.setCode(code);
            itemService.saveCountry(country);
        }
        return "redirect:/admin_country";
    }

    @PostMapping(value = "/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String deleteItem(Model model, @RequestParam(name = "id")Long id){
        Items item = itemService.getItem(id);
        if (item!=null){
            itemService.deleteItem(item);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/delete_country")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCountry(Model model, @RequestParam(name = "id")Long id){
        Countries country = itemService.getCountry(id);
        if (country!=null){
            itemService.deleteCountry(country);
        }
        return "redirect:/admin_country";
    }

    @PostMapping(value = "/delete_brand")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteBrand(Model model, @RequestParam(name = "id")Long id){
        Brands brand = itemService.getBrand(id);
        if (brand!=null){
            itemService.deleteBrand(brand);
        }
        return "redirect:/admin_brand";
    }

    @GetMapping(value = "/admin_category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String category(Model model) {
        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("currentUser", getUserData());
        return "admin_category";
    }

    @PostMapping(value = "/addCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCategory(@RequestParam(name = "category_name")String name,
                               @RequestParam(name = "url")String url) {
        itemService.saveCategory(new Categories(null,name,url));
        return "redirect:/admin_category";
    }

    @PostMapping(value = "/deleteCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCategory(@RequestParam(name = "item_id")Long item_id,
                                  @RequestParam(name = "category_id")Long category_id){
        Categories category = itemService.getCategory(category_id);
        if(category != null) {
            Items item = itemService.getItem(item_id);
            if (item != null) {
                List<Categories> categories = item.getCategories();
                categories.remove(category);
                itemService.saveItem(item);
                return "redirect:/details_item?id="+item_id;
            }
        }
        return "redirect:/";
    }

    @PostMapping(value = "/edit_Category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editCategory(@RequestParam(name = "id")Long id,
                                @RequestParam(name = "name")String name,
                                @RequestParam(name = "logoURL")String url){
        Categories category = itemService.getCategory(id);
        category.setName(name);
        category.setLogoURL(url);
        itemService.saveCategory(category);
        return "redirect:/admin_category";
    }

    @GetMapping(value = "/details_item")
    public String details_items(Model model,@RequestParam(name = "id")Long id){
        Items item = itemService.getItem(id);
        List<Countries> countries = itemService.getAllCountries();
        List<Brands> brands = itemService.getAllBrands();
        List<Categories> categories = itemService.getAllCategories();
        List<Pictures> pictures = itemService.getAllPictures();
        categories = categories.stream()
                .filter(o -> !item.getCategories().contains(o))
                .collect(Collectors.toList());

        List<Comment> comments = itemService.getAllComments();


//        List<Categories> categories1 = new ArrayList<>();
//        for(Categories c : categories){
//            for (Categories cat : item.getCategories()){
//                if (!item.getCategories().contains(c)){
//                    categories1.add(c);
//                }
//            }
//        }
//        categories = categories1;

        model.addAttribute("categories",categories);
        model.addAttribute("brands",brands);
        model.addAttribute("countries",countries);
        model.addAttribute("item",item);
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("pictures", pictures);
        model.addAttribute("comments", comments);
        return "details_item";
    }

    @PostMapping(value = "/assignedCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String assigned(@RequestParam(name = "item_id")Long item_id,
                            @RequestParam(name = "category_id")Long category_id){
        Categories category = itemService.getCategory(category_id);
        if(category != null){
            Items item = itemService.getItem(item_id);
            if(item != null) {
                List<Categories> categories = item.getCategories();
                if (categories == null) {
                    categories = new ArrayList<>();
                }
                categories.add(category);
                itemService.saveItem(item);
                System.out.println(category.getName() + " " + category.getLogoURL());
                return "redirect:/details_item?id="+item_id;
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/search")
    public String search(Model model, @RequestParam(name = "search_name") String name){
        List<Items> item = itemService.findAllByNameContainingOrderByPriceAsc(name);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);
        model.addAttribute("items", item);
        model.addAttribute("search_name", name);
        model.addAttribute("currentUser", getUserData());
        return "search";
    }

    @GetMapping(value = "/sorted")
    public String sorted(Model model,
                          @RequestParam(name = "search_item",defaultValue = "")String search_item,
                          @RequestParam(name = "price_to",defaultValue = "0")double price_to,
                          @RequestParam(name = "price_from",defaultValue = "0")double price_from,
                          @RequestParam(name = "ascending",defaultValue = "no")String sort,
                          @RequestParam(name = "brand_id",defaultValue = "no ")Long brand_id){

        List<Items> items = null;
        Brands brand = itemService.getBrand(brand_id);
        if(brand != null){
            if(search_item.equals("")) {
                if (sort.equals("ascending")) {
                    items = itemService.findAllByBrandOrderByPriceAsc(brand);
                } else {
                    items = itemService.findAllByBrandOrderByPriceDesc(brand);
                }
            }else{
                if (sort.equals("ascending")) {
                    items = itemService.findAllByBrandAndNameOrderByPriceAsc(price_from, price_to, search_item, brand);
                } else {
                    items = itemService.findAllByBrandAndNameOrderByPriceDesc(price_from, price_to, search_item, brand);
                }
            }
        }
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);
        model.addAttribute("items",items);
        model.addAttribute("currentUser", getUserData());
        return "search";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model){
        model.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model){
        model.addAttribute("currentUser", getUserData());
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model){
        model.addAttribute("currentUser", getUserData());
        return "profile";
    }

    @PostMapping(value = "/update_profile")
    @PreAuthorize("isAuthenticated()")
    public String update_profile(Model model,
                                 @RequestParam(name = "id")Long id,
                                 @RequestParam(name = "user_email")String user_email,
                                 @RequestParam(name = "user_full_name")String user_full_name){
        Users user = userService.getUser(id);
        if (user!=null){
            user.setEmail(user_email);
            user.setFullname(user_full_name);
            userService.saveUser(user);
        }

        model.addAttribute("currentUser", getUserData());
        return "profile";
    }

    @PostMapping(value = "/update_password")
    @PreAuthorize("isAuthenticated()")
    public String update_password(Model model,
                                 @RequestParam(name = "id")Long id,
                                 @RequestParam(name = "old_user_password")String old_user_password,
                                 @RequestParam(name = "new_user_password")String new_user_password,
                                 @RequestParam(name = "re_new_password")String re_new_password){
        Users user = userService.getUser(id);
        if (user!=null){
            if (!old_user_password.equals(new_user_password)){
                if (new_user_password.equals(re_new_password)){
                    user.setPassword(new_user_password);
                    userService.saveUser(user);
                }
            }
        }

        model.addAttribute("currentUser", getUserData());
        return "profile";
    }

    private Users getUserData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

    @GetMapping(value = "/register")
    public String register(Model model){
        model.addAttribute("currentUser", getUserData());
        return "register";
    }

    @PostMapping(value = "/register")
    public String toRegister(@RequestParam (name="user_email")String user_email,
                             @RequestParam (name="user_password")String user_password,
                             @RequestParam (name="re_password")String re_password,
                             @RequestParam (name="user_full_name")String user_full_name){
        if (user_password.equals(re_password)){

            Users newUser = new Users();
            newUser.setFullname(user_full_name);
            newUser.setEmail(user_email);
            newUser.setPassword(user_password);

            if (userService.createUser(newUser)!=null){
                return "redirect:/register?success";
            }
        }
        return "redirect:/register?error";
    }

    @GetMapping(value = "/admin_role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String role(Model model) {
        List<Roles> roles = userService.getAllRoles();
        model.addAttribute("roles", roles);

        model.addAttribute("currentUser", getUserData());
        return "admin_role";
    }

    @PostMapping(value = "/add_role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String add_role(@RequestParam(name = "role_name")String role_name,
                           @RequestParam(name = "role_description")String role_description) {
        userService.saveRole(new Roles(null,role_name,role_description));
        return "redirect:/admin_role";
    }

    @GetMapping(value = "/delete_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_role(@RequestParam(name = "user_id")Long user_id,
                             @RequestParam(name = "role_id")Long role_id){
        Roles role = userService.getRole(role_id);
        if(role != null) {
            Users user = userService.getUser(user_id);
            if (user != null) {
                List<Roles> roles = user.getRoles();
                roles.remove(role);
                userService.saveUser(user);
                return "redirect:/details_user?id="+ user_id;
            }
        }

        return "redirect:/admin_user";
    }

    @PostMapping(value = "/edit_role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String edit_role(Model model,
                            @RequestParam(name = "id")Long id,
                            @RequestParam(name = "role_name")String role_name,
                            @RequestParam(name = "role_description")String role_description) {
        List<Roles> roles = userService.getAllRoles();
        model.addAttribute("roles", roles);

        Roles role = userService.getRole(id);
        role.setName(role_name);
        role.setDescription(role_description);
        userService.saveRole(role);
        return "redirect:/admin_role";
    }

    @PostMapping(value = "/delete_Role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete_Role(@RequestParam(name = "id")Long id){
        Roles role = userService.getRole(id);
        if (role!=null){
            userService.deleteRole(role);
        }
        return "redirect:/admin_role";
    }

    @PostMapping(value = "/assignedRoles")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String assignedRoles(Model model,@RequestParam(name = "user_id")Long user_id,
                                @RequestParam(name = "role_id")Long role_id){
        Roles role = userService.getRole(role_id);
        if(role != null){
            Users user = userService.getUser(user_id);
            if(user != null) {
                List<Roles> roles = user.getRoles();
                if (roles == null) {
                    roles = new ArrayList<>();
                }
                roles.add(role);
                userService.saveUser(user);
                return "redirect:/details_user?id="+ user_id;
            }
        }
        return "redirect:/admin_user";

    }

//    @GetMapping(value = "/admin_user")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public String user(Model model) {
//
//        List<Users> users = userService.getAllUsers();
//        model.addAttribute("users", users);
//
//        List<Roles> roles = userService.getAllRoles();
//        model.addAttribute("roles", roles);
//
//        model.addAttribute("currentUser", getUserData());
//        return "admin_user";
//    }


    @PostMapping(value = "/add_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addUser(@RequestParam(name = "full_name")String name,
                          @RequestParam(name = "email")String email,
                          @RequestParam(name = "password")String password){
        Users users = new Users();
        users.setFullname(name);
        users.setEmail(email);
        List<Roles> roles = new ArrayList<>();
        Roles role = userService.getRole(1L);
        roles.add(role);
        users.setRoles(roles);
        users.setPassword(bCryptPasswordEncoder.encode(password));
        userService.saveUser(users);

        return "redirect:/admin_user";
    }

    @GetMapping(value = "/delete_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String deleteUser(@RequestParam(name = "id")Long id){
        Users user = userService.getUser(id);
        if(user != null) {
            userService.delete(user);
        }
        return "redirect:/admin_user";
    }
    @PostMapping(value = "/edit_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String editUser(Model model,@RequestParam(name = "id")Long id,
                           @RequestParam(name = "full_name")String full_name,
                           @RequestParam(name = "email")String email,
                           @RequestParam(name = "password") String password){
        Users user = userService.getUser(id);
        if(user != null){
            user.setFullname(full_name);
            user.setEmail(email);
            user.setPassword(password);
            userService.saveUser(user);
        }

        return "redirect:/details_user?id="+ id;
    }

    @GetMapping(value = "/details_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String details_user(Model model,@RequestParam(name = "id")Long id){
        Users user = userService.getUser(id);
        model.addAttribute("user",user);
        List<Roles> roles = userService.findAllRoles();
        model.addAttribute("roles",roles);
        model.addAttribute("currentUser", getUserData());
        return "details_user";
    }

    @GetMapping(value = "/admin_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_user(Model model){
        model.addAttribute("currentUser",getUserData());
        List<Users> users = userService.getAllUsers();
        List<Roles> roles = userService.findAllRoles();
        model.addAttribute("roles",roles);
        model.addAttribute("users",users);
        return "admin_user";
    }


    @PostMapping(value = "/uploadAvatar")
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(Model model,@RequestParam(name = "user_ava") MultipartFile file){

        if(file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")) {
            try {
                String success = "Successfully saved";
                Users currentUser = getUserData();

                String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName+".jpg");
                Files.write(path, bytes);

                currentUser.setUserAvatar(picName);

                List<Brands> brands = itemService.getAllBrands();
                model.addAttribute("brands",brands);
                model.addAttribute("success_ava",success);
                model.addAttribute("currentUser",getUserData());
                return "profile";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @GetMapping(value = "/viewphoto/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url")String url) throws IOException {
        String pictureUrl = viewPath+defaultPicture;
        if(url != null && !url.equals("null")){
            pictureUrl = viewPath+url+".jpg";
        }

        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
    }

    @PostMapping(value = "/deletePicture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String deletePicture(@RequestParam(name = "item_id")Long item_id,
                                @RequestParam(name = "picture_id")Long picture_id){
        Pictures picture = itemService.getPicture(picture_id);
        itemService.deletePictires(picture);
        return "redirect:/details_item?id="+item_id;
    }

    @GetMapping(value = "/viewphoto_item/{url}",produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto_Item(@PathVariable(name = "url")String url) throws IOException{
        String pictureUrl = viewPath_pictures+defaultPicture;
        if(url != null && !url.equals("null")){
            pictureUrl = viewPath_pictures+url+".jpg";
        }

        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath_pictures+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/basketPage")
    public String basketPage(Model model, HttpServletRequest request, @RequestParam(name = "id")Long id){
        HttpSession session = request.getSession();
        double sum = 0;
        List<Items_Basket> items = (List<Items_Basket>) session.getAttribute("basket");
        if(items == null) {
            items = new ArrayList<>();
            Items item = itemService.getItem(id);
            Items_Basket basketItem = new Items_Basket(id, item.getName(), 1, item.getPrice());
            items.add(basketItem);

        } else {
            Items_Basket basketItem = null;
            for (Items_Basket i : items) {
                if (i.getId().equals(id) ) {
                    basketItem = i;
                    break;
                }
            }
            if (basketItem != null) {
                basketItem.setAmount(basketItem.getAmount() + 1);

            } else {
                Items item = itemService.getItem(id);
                Items_Basket basketItem_other = new Items_Basket(id, item.getName(), 1, item.getPrice());
                basketItem = basketItem_other;
                items.add(basketItem);
            }
        }
        for (Items_Basket i : items) {
            sum = sum + i.getPrice() * i.getAmount();
        }
        int amount_sum = 0;
        for(Items_Basket i: items){
            amount_sum = amount_sum + i.getAmount();
        }
        model.addAttribute("amount",amount_sum);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);
        session.setAttribute("basket",items);
        model.addAttribute("currentUser",getUserData());
        model.addAttribute("sum",sum);
        return "basketPage";
    }
    @PostMapping(value = "/checkIn")
    public String checkIn(HttpServletRequest request,@RequestParam(name = "full_name")String full_name,
                          @RequestParam(name = "card_number")String card_number,
                          @RequestParam(name = "expiration")String expiration,
                          @RequestParam(name = "cvv")String cvv){
        HttpSession session = request.getSession();
        List<Items_Basket> items = (List<Items_Basket>) session.getAttribute("basket");

        Items itemm = new Items();
        double sum = 0;
        for (Items_Basket i : items) {
            sum = sum + i.getPrice()*i.getAmount();
        }
        String items_data = "";
        for(Items_Basket i:items){
            items_data = items_data+i.getName() + " ";
        }

        Items_Basket items_basket = new Items_Basket();
        items_basket.setUser_name(full_name);
        items_basket.setPrice(sum);
        items_basket.setItems_name(items_data);
        //items_basket.setItems(itemm);
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        items_basket.setAddedDate(date);

        itemService.saveItems_Basket(items_basket);
        items.clear();
        session.setAttribute("basket",items);
        //items_basket.setName();

        return "redirect:/";
    }
    @GetMapping(value = "/minusAmount")
    public String minusAmount(Model model,HttpServletRequest request,@RequestParam(name = "id")Long id){
        HttpSession session = request.getSession();
        double sum = 0;
        List<Items_Basket> items = (List<Items_Basket>) session.getAttribute("basket");
        Items_Basket basketItem = null;
        for (Items_Basket i : items) {
            if (i.getId().equals(id) ) {
                basketItem = i;
                break;
            }
        }
        if (basketItem != null) {
            basketItem.setAmount(basketItem.getAmount() - 1);
            if(basketItem.getAmount() == 0){
                items.remove(basketItem);
            }
        }
        for (Items_Basket i : items) {
            sum = sum + i.getPrice()*i.getAmount();
        }
        int amount_sum = 0;
        for(Items_Basket i: items){
            amount_sum = amount_sum + i.getAmount();
        }
        model.addAttribute("amount",amount_sum);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);
        model.addAttribute("sum",sum);
        session.setAttribute("basket",items);
        model.addAttribute("currentUser",getUserData());

        return "basketPage";
    }

    @GetMapping(value = "/clearBasket")
    public String clearBasket(Model model,HttpServletRequest request){
        double sum = 0;
        HttpSession session = request.getSession();
        List<Items_Basket> items = (List<Items_Basket>) session.getAttribute("basket");
        items.clear();
        for (Items_Basket i : items) {
            sum = sum + i.getPrice()*i.getAmount();
        }
        int amount_sum = 0;
        for(Items_Basket i: items){
            amount_sum = amount_sum + i.getAmount();
        }
        model.addAttribute("amount",amount_sum);
        model.addAttribute("sum",sum);
        session.setAttribute("basket",items);
        model.addAttribute("currentUser",getUserData());
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands",brands);
        return "basketPage";
    }

    @PostMapping(value = "/uploadPictureItem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String uploadPictureItem(Model model,@RequestParam(name = "item_picture")MultipartFile file,
                                    @RequestParam(name = "item_id")Long item_id){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        if(file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")) {
            try {
                Pictures picture = new Pictures();
                Items item = itemService.getItem(item_id);
                String success = "Successfully saved";

                String picName = DigestUtils.sha1Hex("avatar_"+file.getOriginalFilename()+"_!Picture");
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath_pictures + picName+".jpg");
                Files.write(path, bytes);

                picture.setUrl(picName);
                picture.setItem(item);
                picture.setAdded_Date(date);
                itemService.addPicture(picture);
                return "redirect:/details_item?id="+item_id;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @GetMapping(value = "/itemsBasket_page")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String itemsBasket_page(Model model){
        List<Items_Basket> items_baskets = itemService.getAllItems_baskets();
        model.addAttribute("baskets_users",items_baskets);
        model.addAttribute("currentUser",getUserData());
        return "itemsBasket_page";
    }

    @PostMapping(value = "/delete_comment")
    @PreAuthorize("isAuthenticated()")
    public String delete_comment(Model model, @RequestParam(name = "id")Long id){
        Comment comment = itemService.getComment(id);
        if (comment!=null){
            itemService.deleteComment(comment);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/admin_delete_comment")
    @PreAuthorize("isAuthenticated()")
    public String admin_delete_comment(Model model, @RequestParam(name = "id")Long id){
        Comment comment = itemService.getComment(id);
        if (comment!=null){
            itemService.deleteComment(comment);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/add_comment")
    @PreAuthorize("isAuthenticated()")
    public String add_comment(Model model, @RequestParam(name = "item_id")Long id,
                              @RequestParam(name = "comment")String comment,
                              @RequestParam(name = "user_id")Long user_id, HttpSession session){
        Users user = userService.getUser(user_id);
        Items item = itemService.getItem(id);
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        //Calendar.getInstance().getTime().getTime()
        if (item!=null){
            Comment comment1 = new Comment(null, comment, date, item, user);
            itemService.addComment(comment1);
        }
        return "redirect:/";
    }

    @PostMapping(value = "/edit_comment")
    @PreAuthorize("isAuthenticated()")
    public String edit_comment(Model model, @RequestParam(name = "comment_id")Long id,
                              @RequestParam(name = "comment")String comment){
        Comment comment1 = itemService.getComment(id);
        comment1.setComment(comment);
        itemService.saveComment(comment1);
        return "redirect:/";
    }
}
