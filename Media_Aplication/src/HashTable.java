import java.util.ArrayList;
import java.util.List;

public class HashTable<K, V> {
    private static final int INITIAL_SIZE = 128;
    private static final double MAX_LOAD_FACTOR = 0.5;
    public  int size = 0;
    private int collisionCount = 0;
    public int tableSize = INITIAL_SIZE;
    public HashEntry<K, V>[] table;

    // Constructor
    public HashTable() {
        table = new HashEntry[tableSize];
    }

    // Hash Function (PAF by default)
    public int hashFunction(K key) {
        String keyStr = key.toString();
        return polynomialAccumulationFunction(keyStr) % tableSize;
    }

    // Polynomial Accumulation Function (PAF)
    private int polynomialAccumulationFunction(String key) {
        int hash = 0;
        int z = 33; // Prime number
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * z + key.charAt(i)) % tableSize;
        }
        return Math.abs(hash);
    }
    public int simpleSummationFunction(String key) {
        int hash = 0;

        // Her karakterin ASCII değerlerini toplama
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i); // ASCII değerini al
        }
        return hash % tableSize; // Hash kodunu tablo boyutuna göre döndür
    }

    // Load Factor Calculation
    public double getLoadFactor() {
        return (double) size / tableSize;
    }

    // Resize Method
    private void resize() {
        int newSize = tableSize * 2;
        HashEntry<K, V>[] oldTable = table;
        table = new HashEntry[newSize];
        tableSize = newSize;
        size = 0;

        for (HashEntry<K, V> entry : oldTable) {
            if (entry != null) {
                addLinearProbing(entry.getKey(), entry.getValue());
            }
        }
        System.out.println("Hash table resized to " + newSize);
    }

    // Çarpışma sayısını döndüren metod
    public int getCollisionCount() {
        return collisionCount;
    }

    // Add with Linear Probing
    public void addLinearProbing(K key, V value) {
        if (getLoadFactor() >= MAX_LOAD_FACTOR) {
            resize();
        }

        int hash = hashFunction(key);
        int index = hash % tableSize;

        // Çarpışmaları saymak için bir flag
        boolean collided = false;

        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                // Aynı IMDb ID bulundu, mevcut veriyi güncelle
                Movie existingMovie = (Movie) table[index].getValue();
                Movie newMovie = (Movie) value;
                updateMovie(existingMovie, newMovie);
                return;
            }
            if (!collided) {
                collisionCount++; // Çarpışma sayısını artır
                collided = true;
            }
            index = (index + 1) % tableSize; // Bir sonraki pozisyona geç
        }

        table[index] = new HashEntry<>(key, value);
        size++;
    }

    // Add with Double Hashing
    public void addDoubleHashing(K key, V value) {
        if (getLoadFactor() >= MAX_LOAD_FACTOR) {
            resize();
        }

        int hash = hashFunction(key);
        int firstHash = hash % tableSize;
        int secondHash = 7 - (hash % 7);
        int index = firstHash;

        // Çarpışmaları saymak için bir flag
        boolean collided = false;

        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                // Aynı IMDb ID bulundu, mevcut veriyi güncelle
                Movie existingMovie = (Movie) table[index].getValue();
                Movie newMovie = (Movie) value;
                updateMovie(existingMovie, newMovie);
                return;
            }
            if (!collided) {
                collisionCount++; // Çarpışma sayısını artır
                collided = true;
            }
            index = (index + secondHash) % tableSize; // Double hashing ilerleme
        }

        table[index] = new HashEntry<>(key, value);
        size++;
    }

    private void updateMovie(Movie existing, Movie newMovie) {
        // URL'yi güncelle
        for (String url : newMovie.getUrl()) {
            if (!existing.getUrl().contains(url)) {
                if (existing.getUrl() == null) {
                    existing.setUrl(new ArrayList<>());
                }
                existing.getUrl().add(url);
            }
        }

        // Platform ve ülkeleri güncelle
        if (existing.getPlatforms() == null) {
            existing.setPlatforms(new ArrayList<>());
        }
        if (existing.getCountries() == null) {
            existing.setCountries(new ArrayList<>());
        }

        for (int i = 0; i < newMovie.getPlatforms().size(); i++) {
            String platform = newMovie.getPlatforms().get(i);
            List<String> countries = newMovie.getCountries().get(i);

            int platformIndex = existing.getPlatforms().indexOf(platform);
            if (platformIndex == -1) {
                // Yeni platform ekle
                existing.getPlatforms().add(platform);
                existing.getCountries().add(new ArrayList<>(countries));
            } else {
                // Mevcut platformun ülkelerini güncelle
                List<String> existingCountries = existing.getCountries().get(platformIndex);
                for (String country : countries) {
                    if (!existingCountries.contains(country)) {
                        existingCountries.add(country);
                    }
                }
            }
        }
    }


    // Remove Method
    public void removeLinearProbing(K key) {
        int hash = hashFunction(key);
        int index = hash;

        // Linear probing ile tabloyu kontrol et
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                table[index] = null;
                size--;
                System.out.println("Media with ImdbId " + key + " has been removed.");
                return;
            }
            index = (index + 1) % tableSize; // Bir sonraki index'e geç
        }

        System.out.println("No media found with ImdbId " + key);
    }

    public void removeDoubleHashing(K key) {
        int hash = hashFunction(key);
        int firstHash = hash % tableSize;
        int secondHash = 7 - (hash % 7); // İkinci hash fonksiyonu
        int index = firstHash;

        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                table[index] = null;
                size--;
                System.out.println("Media with ImdbId " + key + " has been removed.");
                return;
            }
            index = (index + secondHash) % tableSize; // Double hashing ilerleme
        }

        System.out.println("No media found with ImdbId " + key);
    }


    // Get Method
    public V getLinearProbing(K key) {
        int hash = hashFunction(key);
        int index = hash;

        // Linear probing ile tabloyu kontrol et
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                return table[index].getValue(); // Anahtar bulundu, değeri döndür
            }
            index = (index + 1) % tableSize; // Bir sonraki index'e geç
        }

        return null;
    }

    public V getDoubleHashing(K key) {
        int hash = hashFunction(key);
        int firstHash = hash % tableSize;
        int secondHash = 7 - (hash % 7); // İkinci hash fonksiyonu
        int index = firstHash;

        // Double hashing ile tabloyu kontrol et
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                return table[index].getValue(); // Anahtar bulundu, değeri döndür
            }
            index = (index + secondHash) % tableSize; // Double hashing ilerleme
        }

        return null;
    }



    // Linear Probing Method
    public int linearProbing(int hash, K key) {
        int index = hash % tableSize;
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                return index; // Aynı IMDb ID bulundu, mevcut pozisyon döndürülür
            }
            index = (index + 1) % tableSize; // Bir sonraki pozisyona geç
        }
        return index; // Boş bir pozisyon bulundu
    }

    // Double Hashing Method
    public int doubleHashing(int hash, K key) {
        int firstHash = hash % tableSize;
        int secondHash = (7 - (hash % 7)); // İkinci hash için asal sayı kullanıyoruz
        int index = firstHash;
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                return index; // Aynı IMDb ID bulundu, mevcut pozisyon döndürülür
            }
            index = (index + secondHash) % tableSize; // İkinci hash fonksiyonunu kullanarak ilerle
        }
        return index; // Boş bir pozisyon bulundu
    }

    public int SSF(String Id)
    {
    	int newIndex=Id.length(); 
    	return newIndex;
    }
    public int PAF(String Id)
    {	int newIndex=0;
	for(int i=Id.length()-1;i>=0;i--)
	{
		newIndex+=(int)(Id.charAt(i))*Math.pow(33, i-1);
	}
	return newIndex;
}
}
