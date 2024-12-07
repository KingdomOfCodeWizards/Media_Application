import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        // HashTable ve Movie sınıflarını başlatıyoruz
        HashTable<String, Movie> movieHashTable = new HashTable<>();

        // CSV dosyasının yolunu belirtiyoruz
        String filePath = "C:/Users/Lenovo/Downloads/movies_dataset (1).csv"; // Burada dosyanın doğru yolunu belirtin

        // Dosyadan veriyi yükleyelim
        loadData(filePath, movieHashTable);


        Movie thirdMovie=movieHashTable.getDoubleHashing("tt0338013");
        if(thirdMovie!=null)
        {
            thirdMovie.display();
        }


        System.out.println("Toplam çarpışma sayısı: " + movieHashTable.getCollisionCount());
        movieHashTable.getDoubleHashing("tt31282152");
        boolean devam=true;
        while(devam==true)
        {
            System.out.println("1.\tLoad dataset\n" +
                    "2.\tRun 1000 search test\n" +
                    "3.\tSearch for a media item with the ImdbId.\n" +
                    "4.\tList the top 10 media according to user votes\n" +
                    "5.\tList all the media streams in a given country\n" +
                    "6.\tList the media items that are streaming on all 5 platforms\n");
                     String answer=scanner.nextLine();
            switch (answer)
            {
                case "1":
                    System.out.println("Veriler hash table'a aktarıldı.");
                    break;
                case "2":
                    String searchFilePath = "C:/Users/Lenovo/Downloads/search (2).txt";
                    runSearchTest(searchFilePath, movieHashTable);
                    break;
                case "3":
                    System.out.println("Please enter the imdbId no :");
                    String t=scanner.nextLine();
                    Movie test=movieHashTable.getDoubleHashing(t);
                    if(test!=null)
                    {
                        test.display();
                    }
                    else
                    {
                        System.out.println("not Found");
                    }
                    break;
                case "4":
                    System.out.println("Top 10 Movies by Number of Votes:");

                    // Hash table içerisindeki tüm Movie nesnelerini bir listeye alıyoruz
                    List<Movie> allMovies = new ArrayList<>();
                    for (int i = 0; i < movieHashTable.tableSize; i++) {
                        HashEntry<String, Movie> entry = movieHashTable.table[i];
                        if (entry != null) {
                            allMovies.add(entry.getValue());
                        }
                    }

                    // `numberOfVotes` değerine göre sıralama
                    allMovies.sort((m1, m2) -> {
                        int votes1 = parseVotes(m1.getNumberOfVotes());
                        int votes2 = parseVotes(m2.getNumberOfVotes());
                        return Integer.compare(votes2, votes1); // Azalan sırayla sıralama
                    });

                    // En yüksek 10 sonucu yazdırma
                    for (int i = 0; i < Math.min(10, allMovies.size()); i++) {
                        Movie movie = allMovies.get(i);
                        System.out.println((i + 1) + ". " + movie.getTitle() + " (" + movie.getNumberOfVotes() + " votes)");
                    }
                    break;

                case "5":
                    System.out.println("Please enter the country code: ");
                    String countryCode = scanner.nextLine().trim(); // Kullanıcıdan ülke kodunu alıyoruz

                    System.out.println("Movies available in " + countryCode + ":");

                    // Hash table içerisindeki tüm Movie nesnelerini bir listeye alıyoruz
                    List<Movie> moviesInCountry = new ArrayList<>();
                    for (int i = 0; i < movieHashTable.tableSize; i++) {
                        HashEntry<String, Movie> entry = movieHashTable.table[i];
                        if (entry != null) {
                            Movie movie = entry.getValue();
                            // Her film için platformları kontrol et
                            for (int j = 0; j < movie.getPlatforms().size(); j++) {
                                if (movie.getCountries().get(j).contains(countryCode)) {
                                    moviesInCountry.add(movie);
                                    break; // Bu film zaten bulundu, başka bir platform kontrol etmeye gerek yok
                                }
                            }
                        }
                    }

                    // Bulunan filmleri yazdırma
                    if (moviesInCountry.isEmpty()) {
                        System.out.println("No movies found for the specified country code.");
                    } else {
                        for (Movie movie : moviesInCountry) {
                            System.out.println(movie.getTitle());
                        }
                    }
                    break;

                case "6":
                    System.out.println("Movies available on all 5 platforms:");

                    // Tüm platformların isimleri
                    List<String> allPlatforms = Arrays.asList("Netflix", "Amazon Prime", "Apple TV+", "Hulu", "HBO Max");

                    // Hash table içerisindeki tüm Movie nesnelerini bir listeye alıyoruz
                    List<Movie> moviesOnAllPlatforms = new ArrayList<>();
                    for (int i = 0; i < movieHashTable.tableSize; i++) {
                        HashEntry<String, Movie> entry = movieHashTable.table[i];
                        if (entry != null) {
                            Movie movie = entry.getValue();
                            // Her film için platformları kontrol et
                            boolean hasAllPlatforms = true; // Başlangıçta tüm platformların mevcut olduğunu varsayıyoruz

                            for (String platform : allPlatforms) {
                                if (!movie.getPlatforms().contains(platform)) {
                                    hasAllPlatforms = false; // Eğer bir platform yoksa
                                    break; // Kontrolü sonlandır
                                }
                            }

                            if (hasAllPlatforms) {
                                moviesOnAllPlatforms.add(movie); // Tüm platformlara sahip film listeye eklenir
                            }
                        }
                    }

                    // Bulunan filmleri yazdırma
                    if (moviesOnAllPlatforms.isEmpty()) {
                        System.out.println("No movies found that are available on all 5 platforms.");
                    } else {
                        for (Movie movie : moviesOnAllPlatforms) {
                            System.out.println(movie.getTitle());
                        }
                    }
                    break;

                default:
                    break;

            }
        }

    }

    // CSV dosyasını okuyup verileri yükleyen metod
    private static void loadData(String filePath, HashTable<String, Movie> movieHashTable) {
        int successfullyProcessed = 0;
        int failedToProcess = 0;

        // Başlangıç zamanı al
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Başlık satırını atla
            while ((line = br.readLine()) != null) {
                Movie movie = parseMovieLine(line);
                if (movie != null && !movie.getImdb_ID().isEmpty()) {
                    movieHashTable.addDoubleHashing(movie.getImdb_ID(), movie);
                    successfullyProcessed++;
                } else {
                    failedToProcess++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            return;
        }

        // Bitiş zamanı al
        long endTime = System.nanoTime();

        // Toplam süreyi hesapla
        long elapsedTime = endTime - startTime;

        System.out.println("Data loaded successfully!");
        System.out.println("Movies added: " + successfullyProcessed);
        System.out.println("Movies failed to process: " + failedToProcess);
        System.out.println("Indexing Time: " + elapsedTime + " nanoseconds");
    }
    // CSV satırını parse eden metod
    private static Movie parseMovieLine(String line) {
        line = line.replaceAll("\"\"", ""); // Çift tırnak çiftlerini temizle
        String[] parts = splitCSV(line);

        // URL için kontrol
        List<String> urlList = new ArrayList<>();
        if (parts.length > 0 && !parts[0].trim().isEmpty()) {
            urlList.add(parts[0].trim());
        }
        // Başlık
        String title = parts.length > 1 ? parts[1].trim() : "";
        
        // Türü parse etme
        String type = parts.length > 2 ? parts[2].trim() : "";
        
        // Genres parse etme
        List<String> genres = new ArrayList<>(); // Boş bir ArrayList oluştur
        if (parts.length > 3 && !parts[3].trim().isEmpty()) {
            String genreStr = parts[3].replaceAll("\"", "").trim();
            genres.addAll(Arrays.stream(genreStr.split(", "))
                               .filter(g -> !g.isEmpty())
                               .collect(Collectors.toList()));
        }
        
        // Çıkış yılı
        String releaseYear = parts.length > 4 ? parts[4].trim() : "";
        
        // IMDb ID
        String imdbId = parts.length > 5 ? parts[5].trim() : "";
        
        // Rating
        String rating = parts.length > 6 ? parts[6].trim() : "";
        
        // Oy sayısı
        String numberOfVotes = parts.length > 7 ? parts[7].trim() : "";
        
        // Platformlar
        List<String> platforms = new ArrayList<>(); // Boş bir ArrayList oluştur
        if (parts.length > 8 && !parts[8].trim().isEmpty()) {
            platforms.addAll(Arrays.stream(parts[8].split(","))
                                  .map(String::trim)
                                  .collect(Collectors.toList()));
        }
        
        // Ülkeler
        List<List<String>> countries = new ArrayList<>();
        if (parts.length > 9 && !parts[9].trim().isEmpty()) {
            String countryStr = parts[9].replaceAll("\"", "").trim();
            List<String> countryList = new ArrayList<>(Arrays.stream(countryStr.split(","))
                                                 .map(String::trim)
                                                 .collect(Collectors.toList()));
            
            // Her platform için ülke listesi oluştur
            for (int i = 0; i < platforms.size(); i++) {
                countries.add(new ArrayList<>(countryList));
            }
        }

        return new Movie(urlList, title, type, genres, releaseYear, imdbId, rating, numberOfVotes, platforms, countries);
    }

    // CSV satırını doğru şekilde parse eden yardımcı metod
    private static String[] splitCSV(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }

    private static void runSearchTest(String filePath, HashTable<String, Movie> movieHashTable) {
        int totalSearches = 0;
        int foundCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String imdbId;
            long startTime = System.nanoTime(); // Arama işleminin başlangıç zamanı

            while ((imdbId = br.readLine()) != null) {
                // IMDb ID hash tablosunda var mı kontrol et
                Movie movie = movieHashTable.getDoubleHashing(imdbId.trim());
                if (movie != null) {
                    foundCount++; // Bulunan film sayısını artır
                }
                totalSearches++; // Toplam arama sayısını artır
            }

            long endTime = System.nanoTime(); // Arama işleminin bitiş zamanı
            long elapsedTime = endTime - startTime; // Geçen süreyi hesapla

            System.out.println("Search Test Results:");
            System.out.println("Total Searches: " + totalSearches);
            System.out.println("Movies Found: " + foundCount);
            System.out.println("Movies Not Found: " + (totalSearches - foundCount));
            System.out.println("Total Time Taken: " + elapsedTime + " nanoseconds");
            System.out.println("Average Time Per Search: " + (elapsedTime / totalSearches) + " nanoseconds");

        } catch (IOException e) {
            System.out.println("Error reading the search file: " + e.getMessage());
        }
    }
    // Yardımcı metot: numberOfVotes'u parse eden
    private static int parseVotes(String votes) {
        if (votes == null || votes.trim().isEmpty()) {
            return 0; // Boş veya null ise 0 olarak döner
        }
        try {
            return Integer.parseInt(votes);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for votes: " + votes);
            return 0; // Geçersiz format ise 0 döner
        }
    }

}





