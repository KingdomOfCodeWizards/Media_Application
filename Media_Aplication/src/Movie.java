
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Movie {
	private List<String> url;
    private String type;
    private String title;
    private List<String> genres;  // Film türlerini saklayan liste
    private String releaseYear;
    private String imdb_ID;
    private String rating;
    private String numberOfVotes;
    private List<String> platforms;  // Platformların adlarını saklayan liste
    private List<List<String>> countries;  // Her platformun yayınlandığı ülkeleri saklayan liste

    // Constructor
    public Movie(List<String> url, String title, String type, List<String> genres, String releaseYear, String imdb_ID,
            String rating, String numberOfVotes, List<String> platforms, List<List<String>> countries) {
       this.url = new ArrayList<>();
       this.title = title;
       this.type = type;
       this.genres = new ArrayList<>(genres); // Değiştirilebilir liste oluştur
       this.releaseYear = releaseYear;
       this.imdb_ID = imdb_ID;
       this.rating = rating;
       this.numberOfVotes = numberOfVotes;
       this.platforms = new ArrayList<>(platforms); // Değiştirilebilir liste oluştur
       this.countries = new ArrayList<>(countries.stream()
           .map(ArrayList::new) // Her bir iç listeyi de yeni bir ArrayList olarak kopyala
           .collect(Collectors.toList()));
    }

    // Getter ve Setter metotları
    public List<String> getUrl() { return url; }
    public void setUrl(List<String> url) { 
        this.url = url != null ? new ArrayList<>(url) : new ArrayList<>(); 
    }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public String getReleaseYear() { return releaseYear; }
    public void setReleaseYear(String releaseYear) { this.releaseYear = releaseYear; }
    public String getImdb_ID() { return imdb_ID; }
    public void setImdb_ID(String imdb_ID) { this.imdb_ID = imdb_ID; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getNumberOfVotes() { return numberOfVotes; }
    public void setNumberOfVotes(String numberOfVotes) { this.numberOfVotes = numberOfVotes; }
    public List<String> getPlatforms() { return platforms; }
    public void setPlatforms(List<String> platforms) { 
        this.platforms = platforms != null ? new ArrayList<>(platforms) : new ArrayList<>(); 
    }
    public List<List<String>> getCountries() { return countries; }
    public void setCountries(List<List<String>> countries) { 
        this.countries = new ArrayList<>();
        if (countries != null) {
            for (List<String> countryList : countries) {
                this.countries.add(new ArrayList<>(countryList));
            }
        }
    }
    // Movie bilgilerini yazdırma metodu
    public void display() {
        System.out.println("Type: " + type);
        System.out.println("Genre: " + String.join(", ", genres));
        System.out.println("Release Year: " + releaseYear);
        System.out.println("IMDb ID: " + imdb_ID);
        System.out.println("Rating: " + rating);
        System.out.println("Number of Votes: " + numberOfVotes);
        System.out.println();
        System.out.println(platforms.size() + " platforms found for " + title);
        for (int i = 0; i < platforms.size(); i++) {
            System.out.println(platforms.get(i) + " - " + String.join(", ", countries.get(i)));
        }
    }
}
