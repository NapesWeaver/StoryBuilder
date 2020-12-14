import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (args.length == 1) {
			String encoded = encoder.encode(args[0]);
			System.out.println(encoded);
		}
	}
}
