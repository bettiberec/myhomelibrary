package myhomelibrary.model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String summary;
    private double rating;
    private String genre;
    private int year;
    private ReadingStatus status;
    private String coverPath;
    private String spinePath;
    private String notes;
    private int shelfIndex;  // order of the book on the virtual shelf

    public Book(String id, String title, String author){
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = ReadingStatus.TO_READ;
    }

    public Book() {}

   public String getId() {
        return id;
   }
   public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public ReadingStatus getStatus() {
        return status;
    }
    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public String getCoverPath() {
        return coverPath;
    }
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getSpinePath() {
        return spinePath;
    }
    public void setSpinePath(String spinePath) {
        this.spinePath = spinePath;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getShelfIndex(){
        return shelfIndex;
    }
    public void setShelfIndex(int shelfIndex){
        this.shelfIndex = shelfIndex;
    }
}