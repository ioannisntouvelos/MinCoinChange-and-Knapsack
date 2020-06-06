/*\
* 2η Εργασία Αλγορίθμων
* Για την υλοποίηση της λειτουργίας Α (εύρεση ελάχιστου πλήθους VMs ανά πελάτη με 1,2,7,11 πυρήνες  ) χρησιμοποιήθηκε ο αλγόριθμος
* επιστροφής ελάχιστων κερμάτων(VMs) για ένα ποσό n (πυρήνες ), για την υλοποίηση της λειτουργίας Β (εύρεση μέγιστου ποσού πληρωμής
* βάσει των διαθέσιμων πυρήνων ) χρησιμοποιήθηκε ο αλγόριθμος του σάκου για n στοιχεία (πελάτες ) με βάρη w1,...,wn
* (απαιτήσεις σε πυρήνες του κάθε πελάτη  ) , αξίες v1,...,vn (πυρήνες κάθε πελάτη * προσφορά τιμής ανά πυρήνα )
* και χωρητικότητα W (διαθέσιμοι πυρήνες παρόχου.)
* Τα αρχικά δεδομένα εισάγονται σε μια Arraylist <Point2D> και αποθηκέυονται σαν σημεία πραγματικών αριθμών όπου κάθε σημείο
* παριστάνει έναν πελάτη (εκτός από το πρώτο που έχει μόνο σαν x τους διαθέσιμους πόρους) που έχει σαν x τις απαιτήσεις σε πυρήνες
* και σαν y την συνολική τιμή που προσφέρει για αυτούς (πυρήνες κάθε πελάτη * προσφορά τιμής ανά πυρήνα ).
* Χρησιμοποιήθηκε Locale.US διότι ο scanner δεν αναγνώριζε αλλιώς τους πραγματικούς αριθμούς με την τελεία μέσα από το αρχείο .
 * */



import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Locale;


public class Cores {

    private static ArrayList<Point2D> clients = new ArrayList<>(); // Χρησιμοποιείται για αποθήκευση των δεδομένων από το αρχείο
    private static ArrayList<Integer> clientvms = new ArrayList<>(); // Χρησιμοποιείται για αποθήκευση των αποτελεσμάτων για την λειτουργία Α
    private static ArrayList<Integer> possiblecoins = new ArrayList<>();// Χρησιμοποιείται για αποθήκευση των κερμάτων (πυρήνων) που μπορούν να χρησιμοποιηθούν (1,2,7,11)
    private static double [][] sacproblemvalues ;//Χρησιμοποιείται για αποθήκευση των ενδιάμεσων αποτελεσμάτων για την λειτουργία Β
    private static double totalamount =0; // Μεταβλητή για αποθήκευση και εμφάνιση του τελικου αποτέλεσματος της λειτουργίας Β

    /*Μέσω της συνάρτησης solutionfunctionB() δημιουργούμε έναν πίνακα πραγματικών double , διαστάσεων (πλήθος πελατών +1 * πλήθος πυρήνων παρόχου +1 )
    * Στην συνέχεια αρχικοποιούμε τα στοιχεία της πρώτης γραμμής σε 0 κάτι που θα χρειαστεί στον αλγόριθμο του σάκου στην συνέχεια .
    * Τέλος καλούμε την συνάρτηση Knapsack () */
    void solutionfunctionB(){
            double value=0;
            int rows = clients.size()+1;
            int columns = (int)clients.get(0).getX()+1;
            sacproblemvalues = new double[rows][columns];


        for(int j=0;j<columns;j++){
            sacproblemvalues[0][j]=0;
        }

        value=Knapsack(rows-1,columns-1);


    }
    /*Μέσω της συνάρτησης solutionfunctionA εισάγονται οι πιθανοί πυρήνες που μπορεί να έχει ένα VM στην Arraylist possiblecoins
    * και τα ταξινομούνται. Τέλος καλείται για κάθε πελάτη η συνάρτηση MinCoinChange .  */

    void solutionfunctionA(){
        possiblecoins.add(1);
        possiblecoins.add(2);
        possiblecoins.add(7);
        possiblecoins.add(11);
        Collections.sort(possiblecoins);
        for(int i=1;i<clients.size();i++){
            clientvms.add(MinCoinChange((int)clients.get(i).getX()));
            // Γίνεται cast σε int διότι το X του Point θεωρητικά περιέχει double αλλά ξέρουμε ότι το κάθε X είναι int οπότε δεν μας επηρεάζει πουθενά
        }

    }
    /*Συνάρτηση για την καταλλήλη μορφοποίηση και εμφάνιση των αποτελεσμάτων . Για κάθε πελάτη εμφανίζεται το πλήθος των ελάχιστων VMs
    * που βρίσκεται στο αντίστοιχο index της Arraylist clientvms και το max ποσό πληρωμής . */

    void showsolution(){
        for(int i=0;i<clientvms.size();i++){
            System.out.println("Client "+(i+1)+": "+clientvms.get(i)+" VMs");
        }
        System.out.println("Total amount: " + totalamount);

    }


    /*Στην συνάρτηση Knapsack υλοποιούμε τον αλγόριθμο του σάκου βάσει των διαφανειών του μαθήματος , λύνοντας πρώτα τα υποπροβλήματα
    * κι απο τα αποτελέσματα των υποπροβλημάτων φτάνουμε στην τελική λύση . Συγκεκριμένα η συνάρτηση παίρνει σαν όριμα το i που αντιστοιχεί
    * στο ποσό των αντικειμένων και το weight που είναι η μέγιστη χωρητικότητα και ζητάμε την λύση του προβλήματος για αυτές τις δύο τιμές.
    * Σημείωση : Η αξία του κάθε αντικειμένου έχει υπολογιστεί κατευθείαν κατά την εισαγωγή των δεδομένων κι αποτελέι το y σε κάθε Point της
    * Arraylist clients . Στην συνέχεια, ξεκινώντας από το σημείο 1,1 άρα το υποπρόβλημα για 1 αντικέιμενο και 1 μέγιστη χωρητικότητα του σάκου
    * κάνουμε τους εξής ελέγχους : αν το βάρος του αντικειμένου είναι μεγαλύτερο από την εκάστοτε χωρητικότητα j τότε το υποπρόβλημα αυτό έχει
    * σαν λύση την τιμή για ένα λιγότερο αντικέιμενο και ίδια χωρητικότητα. Αλλιως έχει σαν λύση την μέγιστη τιμή απο τις εξής δύο τιμές :
    * την τιμή για ένα λιγότερο αντικέιμενο και ίδια χωρητικότητα και την τιμή για ένα λιγότερο αντικείμενο και χωρητικότητα διαφοράς
    * j - βάρος του αντικειμένου i  συν την αξία του αντικειμένου i . Με βάση αυτήν την λογική συνεχίζουμε και συμπληρώνουμε όλο τον πίνακα
    * και η λύση για το πρόβλημα μας θα βρίσκεται στο σημείο i , weight κι αυτό επιστρέφουμε.  */
    double Knapsack (int i , int weight){


        for(int k=1 ; k <=i ; k++ ){
            for(int j=1;j<=weight;j++){
                if(clients.get(k-1).getX()>j){
                    sacproblemvalues[k][j]=sacproblemvalues[k-1][j];
                }
                else {

                    sacproblemvalues[k][j]= Math.max(sacproblemvalues[k-1][j],sacproblemvalues[k-1][j-(int)clients.get(k-1).getX()]+clients.get(k-1).getY());

                }
            }
        }

        totalamount=sacproblemvalues[i][weight];
        return sacproblemvalues[i][weight];
    }
    /*Η συνάρτηση MinCoinChange υλοποιεί τον αλγόριθμο εύρεσης ελάχιστου πλήθους κερμάτων για ποσό n . Αρχικά δημιουργείται πίνακας
    * ακεραίων με n+1  θέσεις για να αποθηκεύεται σε κάθε θέση το ελάχιστο ποσό των κερμάτων που χρειάζεται για το εκάστοτε ποσό i .
    * Αρχικοποιούμε όλα ,εκτός απο τό πρώτο στοιχείο (το οποίο το βάζουμε 0 διότι για 0 ποσό 0 κέρματα
    * και βάσει αυτού αρχίζουν να συμπληρώνονται τα υπόλοιπα  ) , στην μέγιστη τιμή ακεραίου  ώστε να μπορούμε να διαχωρίζουμε
    * ποια στοιχεία έχουμε ήδη βρεί όταν πηγαίνουμε "προς τα πίσω " . Στην συνέχεια μέσω επανάληψης για κάθε πιθανό πλήθος (index του πίνακα )
    * ελέγχουμε για κάθε πιθανό κέρμα αν είναι ίσο με την τιμή οπότε χρειάζεται μόνο ένα κέρμα αλλιώς αν είναι μικρότερο ελέγχουμε
    * αν έχει ήδη βρεθεί η διαφορά της τιμής με του κέρματος . Αν όχι , προχωράμε στο επόμενο νόμισμα , αν ναι τότε παίρνουμε το ελάχιστο
    * της τιμής που έχει ήδη το index αυτό και της τιμής που υπάρχει στο index της διαφοράς +1 κέρμα .   */

    int MinCoinChange (int n ){
        int [] coinsforvalues = new int[n+1];

        coinsforvalues[0]=0;

        for(int i=1;i<=n;i++){
            coinsforvalues[i]=Integer.MAX_VALUE;
        }
        for(int i=1;i<=n;i++){
            for(int coin :possiblecoins){
                if(coin==i){
                    coinsforvalues[i]=1;
                }
                else if(i>coin){
                    if(coinsforvalues[i-coin]==Integer.MAX_VALUE){
                        continue;
                    }
                    else{
                        coinsforvalues[i]=Math.min(coinsforvalues[i-coin]+1,coinsforvalues[i]);
                    }
                }
            }
        }



        return coinsforvalues[n];
    }
    /*Μέσω της συνάρτησης adddata() εισάγουμε τα δεδομένα που δίνονται από το αρχείο χρησιμοποιώντας την Κλάση Scanner
    * και τα εισάγουμε με μορφή σημείων στην Arraylist clients .*/

    void adddata(String filename) {
        int x;
        double y;
        try {
            Scanner scanner = new Scanner(new File("./" + filename));
            scanner.useLocale(Locale.US); // Αλλαγή Locale για αναγνώριση πραγματικών αριθμών
            x=scanner.nextInt();
            Point sumfocores = new Point(x,0); // Πρώτο σημείο που περιέχει μόνο τους διαθέσιμους αρχικούς πυρήνες
            clients.add(sumfocores);
            while (scanner.hasNext()) {
                x = scanner.nextInt();
                if(scanner.hasNextDouble()) {
                    y = scanner.nextDouble();
                    Point2D client = new Point2D.Double(x, x*y); // Υπολογίζουμε κατευθείαν την αξία των πυρήνων και την εισάγουμε σαν y στο Point
                    clients.add(client);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Η συνάρτηση main του προγράμματος , παίρνουμε σαν όρισμα args[0] το όνομα του αρχείου και μέσω της συνάρτησης adddata()
    * εισάγουμε τα δεδομένα στις κατάλληλες δομές . Στην συνέχεια καλούμε τις συναρτήσεις solutionfunctionA() solutionfunctionB()
    * μέσω των οποίων παίρνουμε τα αποτέλεσματα για κάθε λειτουργία αντίστοιχα και στο τέλος μέσω της συνάρτησης showsolution()
    * τα εμφανίζουμε με την κατάλληλη μορφοποίηση .*/

    public static void main (String args []){

        Cores cores =new Cores();
        cores.adddata(args[0]);
        cores.solutionfunctionA();
        cores.solutionfunctionB();
        cores.showsolution();


    }
}
