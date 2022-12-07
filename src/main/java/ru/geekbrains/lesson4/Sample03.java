package ru.geekbrains.lesson4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Sample03 {

    /**
     * Разработать контракты и компоненты системы "Покупка онлайн билетов на автобус в час пик".
     *
     * 5,6,7,8 - необязательные, опциональные задания.
     *
     * 1.  Предусловия.
     * 2.  Постусловия.
     * 3.  Инвариант.
     * 4.  Определить абстрактные и конкретные классы.
     * 5.  Определить интерфейсы.
     * 6.  Реализовать наследование.
     * 7.  Выявить компоненты.
     * 8.  Разработать Диаграмму компонент использую нотацию UML 2.0. Общая без деталей.
     */
    public static void main(String[] args) {

        Core core = new Core();

        MobileApp mobileApp = new MobileApp(core.getCustomerProvider(), core.getTicketProvider());

        mobileApp.searchTicket(new Date());
        mobileApp.buyTicket("1000000000000044");

        BusStation busStation = new BusStation(core.getTicketProvider());
        busStation.checkTicket("qrcode");

    }

}

class Customer{

    private static int counter;
    private final int id;
    private Collection<Ticket> tickets;

    {
        id = ++counter;
    }

    public int getId() {
        return id;
    }

    public Collection<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Collection<Ticket> tickets) {
        this.tickets = tickets;
    }
}
class Ticket {
    private int id;
    private int customerId;
    private Date date;
    private String qrcode;
    private boolean enable = true;

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

class Database{


    private static int count;
    private Collection<Ticket> tickets = new ArrayList<>();
    private Collection<Customer> customers = new ArrayList<>();

    /**
     * Получить актуальную стоимость билета
     * @return
     */
    public double getTicketAmount(){
        return 45;
    }

    /**
     * Получить идентификатор заявки на покупку билета
     * @return
     */
    public int createTicketOrder(int clientId){
        return  ++count;
    }


    public Collection<Ticket> getTickets() {
        return tickets;
    }

    public Collection<Customer> getCustomers() {
        return customers;
    }
}


class TicketProvider implements actionsWithTicket{
    private final Database database;
    private final PaymentProvider paymentProvider;

    public TicketProvider(Database database, PaymentProvider paymentProvider){
        this.database = database;
        this.paymentProvider = paymentProvider;
    }

    public Collection<Ticket> searchTicket(int clientId, Date date){

        //Предусловие
        if (clientId <= 0){
            throw new RuntimeException("Некорректный id клиента.");
        }

        Collection<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket: database.getTickets()) {
            if (ticket.getCustomerId() == clientId && ticket.getDate().equals(date))
                tickets.add(ticket);
        }

        //Постусловие
        if (tickets == null){
            throw new RuntimeException("Информация о билетах не найдена");
        }
        return tickets;


    }

    public boolean buyTicket(int clientId, String cardNo){

        //Предусловие:
        // TODO: ВОПРОС - В рамках контрактного программирования, мы не проверяем ПЕРЕДАВАЕМЫЕ (в другую модуль) данные.
        //  Не должны ли мы проверить правильность id в методе createTicketOrder(clientId) ???

        if (clientId <= 0){
            throw new RuntimeException("Некорректный id клиента.");
        }

        int orderId = database.createTicketOrder(clientId);
        double amount = database.getTicketAmount();

        //Инвариант
        validateResult(orderId, amount);

        //Постусловие
        try {
            return paymentProvider.buy(orderId, cardNo, amount);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при введении номера карты.");
        }
    }

    public boolean checkTicket(String qrcode){

        //Предусловие
        if (qrcode == null) {
            throw new RuntimeException("QR код некорректен.");
        }

        for (Ticket ticket: database.getTickets()) {
            if (ticket.getQrcode().equals(qrcode)){
                ticket.setEnable(false);
                // Save database ...
                return true;
            }
        }
        return false;
    }

    private void validateResult(int orderId, double amount){
        if (orderId == 0 || amount == 0){
            throw new RuntimeException("Соединение с сервером потеряно.");
        }

    }
}

interface actionsWithTicket{

    /**
     * Позволяет найти билет по id клиента и дате покупки билета
     * @param clientId id клиента
     * @param date дата покупки билета
     * @throws RuntimeException исключение при работе с файлом
     * @return коллекцию билетов
     */
    Collection<Ticket> searchTicket(int clientId, Date date);

    /**
     * Позволяет купить билет по id клиента и номеру карты клиента
     * @param clientId id клиента
     * @param cardNo номер карты клиента
     * @throws RuntimeException исключение при работе с файлом
     * @return tru/false
     */
    boolean buyTicket(int clientId, String cardNo);

    /**
     * Позволяет проверить наличие билета в базе по QR коду
     * @param qrcode QR код
     * @throws RuntimeException исключение при работе с файлом
     * @return tru/false
     */
    boolean checkTicket(String qrcode);
}

class CustomerProvider{
    private final Database database;

    public CustomerProvider(Database database){
        this.database = database;
    }

    public Customer getCustomer(String login, String password){

        return database.getCustomers().stream().findFirst().get();

    }

}

class Core{

    private final CustomerProvider customerProvider;
    private final TicketProvider ticketProvider;
    private final PaymentProvider paymentProvider;
    private final Database database;

    public Core(){
        database = new Database();
        customerProvider = new CustomerProvider(database);
        paymentProvider = new PaymentProvider();
        ticketProvider = new TicketProvider(database, paymentProvider);
    }

    public CustomerProvider getCustomerProvider() {
        return customerProvider;
    }

    public TicketProvider getTicketProvider() {
        return ticketProvider;
    }
}


/**
 * Мобильное приложение
 */
class MobileApp{

    private final TicketProvider ticketProvider;
    private final Customer customer;

    public MobileApp(CustomerProvider customerProvider, TicketProvider ticketProvider){
        this.ticketProvider = ticketProvider;
        customer = customerProvider.getCustomer("login", "password");
    }

    public void searchTicket(Date date){
        customer.setTickets(ticketProvider.searchTicket(customer.getId(), new Date()));
    }


    public boolean buyTicket(String cardNo){
        return ticketProvider.buyTicket(customer.getId(), cardNo);
    }


}

//TODO: ДОМАШНЯЯ РАБОТА
// 1. Доработать модуль BusStation
// 2. Переработать любой модуль, например TicketProvider, в рамках соответствия принципу контрактно-ориентированного программирования.

/**
 * Автобусная станция
 */
class BusStation{

    private final TicketProvider ticketProvider;

    public BusStation(TicketProvider ticketProvider) {
        this.ticketProvider = ticketProvider;
    }

    public boolean checkTicket(String qrcode){
        return ticketProvider.checkTicket(qrcode);
    }

}


class PaymentProvider{

    public boolean buy(int orderId, String cardNo, double amount) {
       return true;
    }

}


class ProcessingCompany{

    private Collection<Bank> banks = new ArrayList<>();

}


class Bank{

}
