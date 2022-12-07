package ru.geekbrains.lesson4;

import java.util.ArrayList;

public class Sample02 {

    public static void main(String[] args) {

        FactoryProvider factoryProvider = new FactoryProvider();
        DealerProvider dealerProvider  = new DealerProvider(factoryProvider);

        ComponentDetails component = dealerProvider.checkComponent(900003);
        System.out.println(component);
    }

}


/**
 * Информация о детали
 */
class ComponentInfo{

    private int id;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComponentInfo(int id, String description) {
        this.id = id;
        this.description = description;
    }
    @Override
    public String toString() {
        return String.format("#%d - %s", id, description);
    }

}

class ComponentDetails{

    private String description;

    public ComponentDetails(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

/**
 * Завод по производству деталей
 */
class FactoryProvider{

    private ArrayList<ComponentInfo> components = new ArrayList<>();

    public FactoryProvider(){
        for (int i = 0; i < 10; i++){
            components.add(new ComponentInfo(900000 + i, String.format("component description %d", 900000 + i)));
        }
        for (int i = 0; i < 5; i++){
            components.add(new ComponentInfo(1000 + i, String.format("component description %d", 1000 + i)));
        }
    }

    public ComponentInfo getComponentInfo(int id){

        //Предусловие
        if (id <= 0)
            throw new RuntimeException("Некорректный номер детали.");
        if (String.valueOf(id).length() < 6)
            throw new RuntimeException("Некорректный номер детали. Деталь существует, но устарела и более не выпускается.");

        ComponentInfo searchComponent = null;
        for (ComponentInfo component : components) {
            if (component.getId() == id){
                searchComponent = component;
                break;
            }
        }

        //Постусловие
        if (searchComponent == null)
            throw new RuntimeException("Деталь не найдена.");


        return searchComponent;
    }


}


/**
 * Дилер
 */
class DealerProvider{

    private final FactoryProvider factoryProvider;

    DealerProvider(FactoryProvider factoryProvider){
        this.factoryProvider = factoryProvider;
    }


    public ComponentDetails checkComponent(int id){

        //Предусловие

        // Реализация метода
        //TODO: В рамках контрактного программирования, мы не проверяем ПЕРЕДАВАЕМЫЕ (в другой модуль) данные
        /*if (id < 0 || String.valueOf(id).length() < 6)
            throw new RuntimeException("Некорректный номер детали.");*/
        ComponentInfo component = factoryProvider.getComponentInfo(id);
        // .....
        ComponentDetails componentDetails = new ComponentDetails(component.getDescription());
        //.....


        //Постусловие
        if (componentDetails == null)
            throw new RuntimeException("Информация о детали не найдена.");


        return componentDetails;
    }

}





















