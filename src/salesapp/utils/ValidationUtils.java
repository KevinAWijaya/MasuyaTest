package salesapp.utils;

public class ValidationUtils {

    /**
     * Mengecek apakah string hanya mengandung huruf dan angka (alphanumeric).
     *
     * @param text teks yang ingin dicek
     * @return true jika alphanumeric, false jika ada simbol/spasi
     */
    public static boolean isAlphanumeric(String text) {
        return text != null && text.matches("[a-zA-Z0-9]+");
    }

}
