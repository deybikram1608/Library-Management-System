import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;


class Book {
    private String bookName;
    private String authorName;
    private String issuerName;
    private LocalDateTime issuedOn;
    private LocalDateTime returnedOn;

    public Book(String bookName , String authorName){
        this.bookName=bookName;
        this.authorName=authorName;
    }

    public String getbookName(){
        return bookName;
    }

    public String getauthorName(){
        return authorName;
    }

    public void issue(String issuerName){
        this.issuerName=issuerName;
        this.issuedOn= LocalDateTime.now();
    }

    public String getissuerName(){
        return issuerName;
    }

    public String getissuedOn(){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy   HH:mm:ss");
        return issuedOn.format(f);
    }

    public void returnbk(){
        this.issuerName= null;
        this.returnedOn = LocalDateTime.now();
    }

    public LocalDateTime getIssuedOnRaw(){
        return issuedOn;
    }

    public LocalDateTime getReturnedOnRaw(){
        return returnedOn;
    }

    public void setIssuedOn(LocalDateTime time){
        this.issuedOn= time;
    }

    public void setReturnedOn(LocalDateTime time){
        this.returnedOn= time;
    }

    public void setIssuerName(String name){
        this.issuerName= name;
    }

    public String getreturnedOn(){
        if(returnedOn==null)
            return "Not returned.";
        DateTimeFormatter g = DateTimeFormatter.ofPattern("dd/MM/yyyy   hh:mm:ss a");
        return returnedOn.format(g);
    }



    public String toString(){
        return "Name of the book: "+bookName +"\nName of the author: "+authorName+"\nIssued to: "+(issuerName == null?"Not Issued": issuerName+" on "+ getissuedOn())+"\nBook returned on: "+ getreturnedOn()+"\n";
    }
}

class Library{
    private ArrayList<Book> l;
    public Library(){
        l = new ArrayList<>();
    }

    public void addBooks(Book b){
        try{
            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO books(book_name,author_name) VALUES(?,?);";

            PreparedStatement ps= con.prepareStatement(query);
            ps.setString(1, b.getbookName());
            ps.setString(2, b.getauthorName());
            ps.executeUpdate();

            System.out.println("Book added to database successfully");

            con.close();
        }catch(Exception e){
            System.out.println("Error adding Book to database: "+e.getMessage());
        }
    }

    public void showBooks(){
        try{
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            String query = "SELECT * FROM books";
            ResultSet rs = st.executeQuery(query);

            while(rs.next()){
                System.out.println("\nBook Name: "+rs.getString("book_name")+"\nAuthor: "+rs.getString("author_name")+
                        "\nIssued to: "+rs.getString("issuer_name")+"\nIssued On: "+rs.getString("issued_on")
                        +"\nReturned On: "+ rs.getString("returned_on"));
                System.out.println();
            }
            con.close();
        }catch(Exception e){
            System.out.println("Error showing Books from database: "+e.getMessage());
        }
    }



    public void issueBook(String bookName, String issuer){
       try{
           Connection con = DBConnection.getConnection();
           String query = "UPDATE books SET issuer_name=?, issued_on=? WHERE book_name=? AND issuer_name IS NULL";

           PreparedStatement ps = con.prepareStatement(query);
           ps.setString(1, issuer);
           ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
           ps.setString(3, bookName);

           int rows = ps.executeUpdate();
           if(rows>0){
               System.out.println("Book issued successfully.");
           }
           else{
               System.out.println("Book not found or already issued.");
           }
           con.close();
       } catch (Exception e) {
           System.out.println("Error issueBook: "+e.getMessage());
       }
    }


    public void returnBook(String bookName){
     try{
         Connection con = DBConnection.getConnection();
         String query = "UPDATE books SET issuer_name=NULL, returned_on=? WHERE book_name=? AND issuer_name IS NOT NULL";

         PreparedStatement ps = con.prepareStatement(query);
         ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
         ps.setString(2, bookName);

         int rows = ps.executeUpdate();
         if(rows>0){
             System.out.println("Book returned successfully.");
         }
         else{
             System.out.println("Book not found or not issued.");
         }
         con.close();
        }catch(Exception e){
         System.out.println("Error returnBook: "+e.getMessage());
     }

    }
}




public class LibraryManagement {
    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();
        if(con !=null){
            System.out.println("Connection Successful");
        }
        else{
            System.out.println("Connection Failed");
        }
        Scanner sc = new Scanner(System.in);
        Library lib = new Library();

        System.out.println("LIBRARY MANAGEMENT SYSTEM\n");

        boolean flag= true;
        while(flag){
            System.out.println("\nMENU:");
            System.out.println("1.Add books\n2.View Books\n3.Issue Books\n4.Return Books\n5.Exit\n");

            System.out.print("\nEnter your choice: ");
            int x= sc.nextInt();
            sc.nextLine();

            switch(x){
                case 1:
                    System.out.print("Enter the name of the book: ");
                    String bookName = sc.nextLine();

                    System.out.print("Enter the name of the Author: ");
                    String author = sc.nextLine();

                    Book y = new Book(bookName, author);
                    lib.addBooks(y);
                    break;

                case 2:
                    System.out.println("\nThe books avaliable are: \n");
                    lib.showBooks();
                    break;

                case 3:
                    System.out.print("Enter the name the book issued: ");
                    String k = sc.nextLine();

                    System.out.print("Enter the name of the issuer: ");
                    String name = sc.nextLine();

                    lib.issueBook(k, name);
                    break;

                case 4:
                    System.out.print("Entered the name of the book returned: ");
                    String br = sc.nextLine();

                    lib.returnBook(br);
                    break;

                case 5:
                    flag = false;
                    System.out.println("Exiting Application.");
                    break;

                default:
                    System.out.println("Wrong choice.");
                    break;
            }
        }
        sc.close();
    }
}


