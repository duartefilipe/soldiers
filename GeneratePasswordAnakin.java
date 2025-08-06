import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordAnakin {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "eumesmo";
        String encodedPassword = encoder.encode(password);
        System.out.println("Senha original: " + password);
        System.out.println("Senha BCrypt: " + encodedPassword);
        System.out.println();
        System.out.println("INSERT INTO tb_user (name, email, password, role, criado_em) VALUES");
        System.out.println("('Anakin', 'anakin@anakin.com', '" + encodedPassword + "', 'ADMIN', NOW());");
    }
} 