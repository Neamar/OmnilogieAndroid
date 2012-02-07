package fr.omnilogie.app;

/**
 * Interface pour faire les classes nécessitant un callback.
 * La méthode générique callback(Object) sera appelé pour effectuer le callback.
 * 
 * @author Benoit
 *
 */
public interface CallbackObject {
	
	/**
	 * Méthode qui peut être utilisée en tant que callback.  
	 * 
	 * @param objet disponible pour un paramètre de retour
	 */
	void callback(Object objet);
}
