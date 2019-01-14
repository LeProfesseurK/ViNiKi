package viniki.ccm.insset.viniki;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BDDManager {
    private static String NomTableUtilisateur = "Utilisateur";

    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static void addUtilisateur(Utilisateur newUtilisateur, String password, final Inscription context){

        Map<String, Object> nouveauUtilisateur = new HashMap<>();

        nouveauUtilisateur.put("nomUtilisateur", newUtilisateur.getNomUtilisateur());
        nouveauUtilisateur.put("prenomUtilisateur", newUtilisateur.getPrenomUtilisateur());
        nouveauUtilisateur.put("emailUtilisateur", newUtilisateur.getEmailUtilisateur());
        nouveauUtilisateur.put("adresseUtilisateur", newUtilisateur.getAdresseUtilisateur());
        nouveauUtilisateur.put("dateNaissanceUtilisateur", newUtilisateur.getDateNaissanceUtilisateur());
        nouveauUtilisateur.put("password", password);

        firebaseFirestore
                //nom de la base
                .collection(NomTableUtilisateur)
                .add(nouveauUtilisateur) //Ajout de la map
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.i("Ajoute_Map_Activite", "Add OK");
                        Log.i("Ajoute_Map_Activite", task.getResult().getId());
                        context.inscriptionSuccess(task.getResult().getId());
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Ajoute_Map_Activite", "Add Echec");
                        context.inscriptionEchec();
                    }
                });
    }

    public static void loginUtilisateur(String login, String mdp, final Login context){
        Task<QuerySnapshot> taskQuery = firebaseFirestore
                .collection(NomTableUtilisateur)
                .whereEqualTo("emailUtilisateur", login)
                .whereEqualTo("password", mdp)
                .get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utilisateur utilisateur = new Utilisateur();
                        if(task.getResult().size() != 1){
                            // Pas la bonne récupération.
                            context.loginEchec();
                        }else{
                            // Utilisateur trouvé.
                            DocumentSnapshot result = task.getResult().getDocuments().get(0);
                            utilisateur.setIdUtilisateur(result.getId());
                            utilisateur.setNomUtilisateur((String) result.get("nomUtilisateur"));
                            utilisateur.setPrenomUtilisateur((String) result.get("prenomUtilisateur"));
                            utilisateur.setEmailUtilisateur((String) result.get("nomUtilisateur"));
                            utilisateur.setAdresseUtilisateur((String) result.get("nomUtilisateur"));
                            utilisateur.setDateNaissanceUtilisateur((Date) result.get("dateNaissanceUtilisateur"));

                            GlobalVariable.getInstance().setConnectedUtilisateur(utilisateur);

                            context.loginSuccess();
                        }

                    }
                });
    }

    public static void emailExiste(String email, final Inscription context){
        Task<QuerySnapshot> taskQuery = firebaseFirestore
                .collection(NomTableUtilisateur)
                .whereEqualTo("emailUtilisateur", email)
                .get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Utilisateur utilisateur = new Utilisateur();
                        if(task.getResult().size() != 0){
                            // Existant
                            context.emailUncheck();
                        }else{
                            // Nouveau
                            context.emailCheck();
                        }
                    }
                });
    }

}
