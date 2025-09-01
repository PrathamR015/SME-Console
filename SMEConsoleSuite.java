import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * SMEConsoleSuite
 * ----------------
 * A single-file Java console application that demonstrates Data Structures & Algorithms (DSA)
 * to solve common SME problems: Inventory, Finance (cash flow), CRM (lead dedupe), and
 * Workflow planning (DAG + critical path). Designed for quick compilation and learning.
 *
 * How to run (JDK 11+):
 *   javac SMEConsoleSuite.java
 *   java SMEConsoleSuite
 */
public class SMEConsoleSuite {

    // ==== ENTRY POINT ======================================================
    public static void main(String[] args) {
        new SMEConsoleSuite().run();
    }

    private final Scanner sc = new Scanner(System.in);

    // Core Modules
    private final InventoryManager inventory = new InventoryManager();
    private final FinanceManager finance = new FinanceManager();
    private final CRMManager crm = new CRMManager();
    private final WorkflowManager workflow = new WorkflowManager();

    private void run() {
        seedDemoData();
        println("\n⚙️  SME Problem Solver — Console DSA Suite");
        println("(Inventory • Finance • CRM • Workflow)\n");
        while (true) {
            println("MAIN MENU");
            println("1) Inventory Management (Trie + Heaps)" );
            println("2) Finance & Cash Flow (PQ + Knapsack)" );
            println("3) CRM & Lead Dedupe (Levenshtein)" );
            println("4) Workflow Planner (DAG + Critical Path)" );
            println("5) Quick Analytics" );
            println("0) Exit");
            int choice = askInt("Choose option:");
            switch (choice) {
                case 1 -> inventoryMenu();
                case 2 -> financeMenu();
                case 3 -> crmMenu();
                case 4 -> workflowMenu();
                case 5 -> analyticsMenu();
                case 0 -> {
                    println("Goodbye! 👋");
                    return;
                }
                default -> println("Invalid choice. Try again.\n");
            }
        }
    }

    // ==== INVENTORY MENU ===================================================
    private void inventoryMenu() {
        while (true) {
            println("\nINVENTORY");
            println("1) Add product");
            println("2) Update stock (purchase/adjust)");
            println("3) Sell product (reduces stock)");
            println("4) Search product by prefix (Trie)");
            println("5) Show low-stock alerts (Min-Heap)");
            println("6) List all products (sorted by name)");
            println("0) Back");
            int c = askInt("Choose option:");
            if (c == 0) return;
            switch (c) {
                case 1 -> {
                    String name = askStr("Name:");
                    String cat = askStr("Category:");
                    double price = askDouble("Price:");
                    int stock = askInt("Initial stock:");
                    int reorder = askInt("Reorder level (alert below):");
                    Product p = inventory.addProduct(name, cat, price, stock, reorder);
                    println("Added product: " + p);
                }
                case 2 -> {
                    int id = askInt("Product ID:");
                    int delta = askInt("Add to stock (use negative to adjust down):");
                    boolean ok = inventory.updateStock(id, delta);
                    println(ok ? "Updated." : "Product not found.");
                }
                case 3 -> {
                    int id = askInt("Product ID to sell:");
                    int qty = askInt("Quantity:");
                    boolean ok = inventory.sellProduct(id, qty);
                    println(ok ? "Sale recorded." : "Failed (check stock/id).\n");
                }
                case 4 -> {
                    String prefix = askStr("Prefix:");
                    List<Product> res = inventory.searchByPrefix(prefix);
                    if (res.isEmpty()) println("No matches.");
                    else res.forEach(System.out::println);
                }
                case 5 -> {
                    List<Product> alerts = inventory.lowStockAlerts(5);
                    if (alerts.isEmpty()) println("No low-stock items right now.");
                    else {
                        println("Low-stock (top 5 by stock):");
                        alerts.forEach(System.out::println);
                    }
                }
                case 6 -> inventory.listAll().forEach(System.out::println);
                default -> println("Invalid.");
            }
        }
    }

    // ==== FINANCE MENU =====================================================
    private void financeMenu() {
        while (true) {
            println("\nFINANCE & CASH FLOW");
            println("1) Add RECEIVABLE (invoice to collect)");
            println("2) Add PAYABLE (bill to pay)");
            println("3) Show upcoming items (by due date)");
            println("4) Cash runway projection (days) ❱ current cash");
            println("5) Choose bills to pay under cash limit (0/1 Knapsack)");
            println("0) Back");
            int c = askInt("Choose option:");
            if (c == 0) return;
            switch (c) {
                case 1 -> {
                    double amt = askDouble("Amount:");
                    LocalDate due = askDate("Due date (yyyy-MM-dd):");
                    finance.addReceivable(amt, due);
                    println("Receivable added.");
                }
                case 2 -> {
                    double amt = askDouble("Amount:");
                    LocalDate due = askDate("Due date (yyyy-MM-dd):");
                    int impact = askInt("Impact score (1-100, higher = more critical):");
                    finance.addPayable(amt, due, impact);
                    println("Payable added.");
                }
                case 3 -> {
                    List<FinanceItem> upcoming = finance.upcoming(10);
                    if (upcoming.isEmpty()) println("Nothing due soon.");
                    else upcoming.forEach(System.out::println);
                }
                case 4 -> {
                    double currentCash = askDouble("Current cash on hand:");
                    int days = askInt("Project for how many days (e.g., 30):");
                    double net = finance.projectCashFlow(currentCash, days);
                    println(String.format("Projected cash after %d days: %.2f", days, net));
                }
                case 5 -> {
                    double budget = askDouble("Cash available for paying bills today:");
                    List<Payable> chosen = finance.pickPayablesToPay(budget);
                    double spend = chosen.stream().mapToDouble(p -> p.amount).sum();
                    int totalImpact = chosen.stream().mapToInt(p -> p.impactScore).sum();
                    println("Pay these bills now (maximizing impact within budget):");
                    chosen.forEach(System.out::println);
                    println(String.format("Total spend: %.2f | Total impact saved: %d", spend, totalImpact));
                }
                default -> println("Invalid.");
            }
        }
    }

    // ==== CRM MENU =========================================================
    private void crmMenu() {
        while (true) {
            println("\nCRM / LEAD MANAGEMENT");
            println("1) Add lead (with dedupe suggestions)");
            println("2) List leads");
            println("3) Find similar leads by name/email (Levenshtein)");
            println("0) Back");
            int c = askInt("Choose option:");
            if (c == 0) return;
            switch (c) {
                case 1 -> {
                    String name = askStr("Name:");
                    String email = askStr("Email:");
                    String phone = askStr("Phone:");
                    Lead candidate = new Lead(name, email, phone);
                    List<Lead> similar = crm.findSimilar(candidate, 2); // distance <= 2
                    if (!similar.isEmpty()) {
                        println("⚠️  Possible duplicates found:");
                        similar.forEach(l -> println("  • " + l));
                    }
                    Lead added = crm.addLead(candidate);
                    println("Saved: " + added);
                }
                case 2 -> crm.listLeads().forEach(System.out::println);
                case 3 -> {
                    String q = askStr("Enter name or email to check:");
                    Lead probe = new Lead(q, q, "");
                    List<Lead> similar = crm.findSimilar(probe, 2);
                    if (similar.isEmpty()) println("No close matches.");
                    else similar.forEach(System.out::println);
                }
                default -> println("Invalid.");
            }
        }
    }

    // ==== WORKFLOW MENU ====================================================
    private void workflowMenu() {
        while (true) {
            println("\nWORKFLOW PLANNER (DAG)");
            println("1) Add task");
            println("2) Add dependency (A depends on B)");
            println("3) Show topological order");
            println("4) Show critical path (max duration path)");
            println("0) Back");
            int c = askInt("Choose option:");
            if (c == 0) return;
            switch (c) {
                case 1 -> {
                    String name = askStr("Task name:");
                    int days = askInt("Duration (days):");
                    Task t = workflow.addTask(name, days);
                    println("Added: " + t);
                }
                case 2 -> {
                    int a = askInt("Task ID (A):");
                    int b = askInt("Depends on Task ID (B):");
                    boolean ok = workflow.addDependency(a, b);
                    println(ok ? "Dependency added." : "Invalid IDs or cycle detected.");
                }
                case 3 -> {
                    List<Task> order = workflow.topologicalOrder();
                    if (order == null) println("Graph has a cycle. Fix dependencies.");
                    else order.forEach(System.out::println);
                }
                case 4 -> {
                    WorkflowManager.CriticalPathResult res = workflow.criticalPath();
                    if (res == null) {
                        println("Graph has a cycle. Fix dependencies.");
                    } else {
                        println("Critical Path (duration " + res.totalDuration + " days):");
                        res.path.forEach(t -> println("  -> " + t));
                    }
                }
                default -> println("Invalid.");
            }
        }
    }

    // ==== ANALYTICS MENU ===================================================
    private void analyticsMenu() {
        println("\nQUICK ANALYTICS");
        println("• #Products: " + inventory.count());
        println("• #Low-stock items: " + inventory.lowStockAlerts(1000).size());
        println("• #Leads: " + crm.listLeads().size());
        println("• #Tasks: " + workflow.taskCount());
        println("• Upcoming finance items (next 5):");
        finance.upcoming(5).forEach(System.out::println);
    }

    // ==== DEMO DATA ========================================================
    private void seedDemoData() {
        // Inventory
        inventory.addProduct("Apple iPhone 15 Case", "Accessories", 799.0, 5, 3);
        inventory.addProduct("Apple iPhone 15 Charger", "Accessories", 1499.0, 15, 5);
        inventory.addProduct("Samsung S24 Case", "Accessories", 699.0, 2, 5);
        inventory.addProduct("USB-C Cable 1m", "Cables", 299.0, 25, 10);
        inventory.addProduct("USB-C Cable 2m", "Cables", 399.0, 8, 10);

        // Finance
        finance.addReceivable(35000, LocalDate.now().plusDays(7));
        finance.addReceivable(18000, LocalDate.now().plusDays(3));
        finance.addPayable(12000, LocalDate.now().plusDays(2), 60);
        finance.addPayable(8000, LocalDate.now().plusDays(5), 30);
        finance.addPayable(15000, LocalDate.now().plusDays(10), 80);

        // CRM
        crm.addLead(new Lead("Rohit Sharma", "rohit@xyz.com", "9876543210"));
        crm.addLead(new Lead("Rahul Verma", "rahul.v@abc.in", "9898989898"));
        crm.addLead(new Lead("Rohit Sarma", "rohit.sarma@xyz.com", "9000090000"));

        // Workflow
        Task t1 = workflow.addTask("Collect requirements", 3);
        Task t2 = workflow.addTask("Set up inventory", 2);
        Task t3 = workflow.addTask("Integrate billing", 4);
        Task t4 = workflow.addTask("Test & train staff", 2);
        workflow.addDependency(t2.id, t1.id);
        workflow.addDependency(t3.id, t2.id);
        workflow.addDependency(t4.id, t3.id);
    }

    // ==== IO HELPERS =======================================================
    private static void println(String s) { System.out.println(s); }

    private String askStr(String prompt) {
        System.out.print(prompt + " ");
        return sc.nextLine().trim();
    }

    private int askInt(String prompt) {
        while (true) {
            try {
                String s = askStr(prompt);
                return Integer.parseInt(s);
            } catch (Exception e) {
                println("Enter a valid integer.");
            }
        }
    }

    private double askDouble(String prompt) {
        while (true) {
            try {
                String s = askStr(prompt);
                return Double.parseDouble(s);
            } catch (Exception e) {
                println("Enter a valid number.");
            }
        }
    }

    private LocalDate askDate(String prompt) {
        while (true) {
            try {
                String s = askStr(prompt);
                return LocalDate.parse(s);
            } catch (Exception e) {
                println("Use yyyy-MM-dd.");
            }
        }
    }
}

// ============================================================================
// INVENTORY MODULE (Trie + Min-Heap alerts)
// ============================================================================
class InventoryManager {
    private final Map<Integer, Product> byId = new HashMap<>();
    private final TreeMap<String, Integer> nameToId = new TreeMap<>(); // sorted listing
    private final ProductNameTrie trie = new ProductNameTrie();
    private final PriorityQueue<Product> lowStockMinHeap = new PriorityQueue<>(Comparator
            .comparingInt((Product p) -> p.stock)
            .thenComparingInt(p -> p.id));
    private int nextId = 1;

    public Product addProduct(String name, String category, double price, int stock, int reorderLevel) {
        Product p = new Product(nextId++, name, category, price, stock, reorderLevel);
        byId.put(p.id, p);
        nameToId.put(p.name.toLowerCase(), p.id);
        trie.insert(p.name.toLowerCase(), p.id);
        lowStockMinHeap.offer(p);
        return p;
    }

    public boolean updateStock(int id, int delta) {
        Product p = byId.get(id);
        if (p == null) return false;
        // Update heap: remove-then-reinsert pattern (heap doesn't auto-adjust)
        lowStockMinHeap.remove(p);
        p.stock += delta;
        lowStockMinHeap.offer(p);
        return true;
    }

    public boolean sellProduct(int id, int qty) {
        Product p = byId.get(id);
        if (p == null || qty <= 0 || p.stock < qty) return false;
        lowStockMinHeap.remove(p);
        p.stock -= qty;
        lowStockMinHeap.offer(p);
        return true;
    }

    public List<Product> searchByPrefix(String prefix) {
        Set<Integer> ids = trie.searchPrefix(prefix.toLowerCase());
        List<Product> res = new ArrayList<>();
        for (Integer id : ids) {
            Product p = byId.get(id);
            if (p != null) res.add(p);
        }
        // sort by name for stable output
        res.sort(Comparator.comparing(pr -> pr.name.toLowerCase()));
        return res;
    }

    public List<Product> lowStockAlerts(int limit) {
        // Pull up to 'limit' lowest-stock items below their reorder level
        List<Product> result = new ArrayList<>();
        // Copy heap to avoid disturbing original order
        PriorityQueue<Product> copy = new PriorityQueue<>(lowStockMinHeap);
        while (!copy.isEmpty() && result.size() < limit) {
            Product p = copy.poll();
            if (p.stock <= p.reorderLevel) result.add(p);
        }
        return result;
    }

    public List<Product> listAll() {
        List<Product> res = new ArrayList<>();
        for (String name : nameToId.keySet()) {
            Product p = byId.get(nameToId.get(name));
            if (p != null) res.add(p);
        }
        return res;
    }

    public int count() { return byId.size(); }
}

class Product {
    public final int id;
    public final String name;
    public final String category;
    public final double price;
    public int stock;
    public final int reorderLevel;

    public Product(int id, String name, String category, double price, int stock, int reorderLevel) {
        this.id = id; this.name = name; this.category = category; this.price = price; this.stock = stock; this.reorderLevel = reorderLevel;
    }

    @Override public String toString() {
        return String.format("[#%d] %s | %s | ₹%.2f | stock=%d (reorder≤%d)", id, name, category, price, stock, reorderLevel);
    }
}

class ProductNameTrie {
    private final Node root = new Node();

    private static class Node {
        Map<Character, Node> child = new HashMap<>();
        boolean isWord;
        Set<Integer> idsAtWord = new HashSet<>();
    }

    public void insert(String word, int productId) {
        Node cur = root;
        for (char ch : word.toCharArray()) {
            cur = cur.child.computeIfAbsent(ch, k -> new Node());
        }
        cur.isWord = true;
        cur.idsAtWord.add(productId);
    }

    public Set<Integer> searchPrefix(String prefix) {
        Node cur = root;
        for (char ch : prefix.toCharArray()) {
            cur = cur.child.get(ch);
            if (cur == null) return Collections.emptySet();
        }
        Set<Integer> res = new HashSet<>();
        dfsCollect(cur, res);
        return res;
    }

    private void dfsCollect(Node node, Set<Integer> out) {
        if (node.isWord) out.addAll(node.idsAtWord);
        for (Node nxt : node.child.values()) dfsCollect(nxt, out);
    }
}

// ============================================================================
// FINANCE MODULE (PriorityQueues + 0/1 Knapsack)
// ============================================================================
class FinanceManager {
    private int nextId = 1;
    private final PriorityQueue<FinanceItem> byDueDate = new PriorityQueue<>(Comparator
            .comparing((FinanceItem f) -> f.dueDate)
            .thenComparingInt(f -> f.id));

    // For knapsack we keep a separate list of payables
    private final List<Payable> payables = new ArrayList<>();
    private final List<Receivable> receivables = new ArrayList<>();

    public void addReceivable(double amount, LocalDate due) {
        Receivable r = new Receivable(nextId++, amount, due);
        byDueDate.offer(r);
        receivables.add(r);
    }

    public void addPayable(double amount, LocalDate due, int impactScore) {
        Payable p = new Payable(nextId++, amount, due, impactScore);
        byDueDate.offer(p);
        payables.add(p);
    }

    public List<FinanceItem> upcoming(int k) {
        List<FinanceItem> res = new ArrayList<>();
        PriorityQueue<FinanceItem> copy = new PriorityQueue<>(byDueDate);
        while (!copy.isEmpty() && res.size() < k) res.add(copy.poll());
        return res;
    }

    /**
     * Very simple cash projection: sum of receivables minus payables that fall within 'days'.
     */
    public double projectCashFlow(double currentCash, int days) {
        LocalDate horizon = LocalDate.now().plusDays(days);
        double cash = currentCash;
        for (Receivable r : receivables) if (!r.dueDate.isAfter(horizon)) cash += r.amount;
        for (Payable p : payables) if (!p.dueDate.isAfter(horizon)) cash -= p.amount;
        return cash;
    }

    /**
     * Choose subset of payables to pay under budget maximizing impactScore.
     * Classic 0/1 knapsack on (cost=amount, value=impactScore).
     */
    public List<Payable> pickPayablesToPay(double budget) {
        int n = payables.size();
        int W = (int)Math.round(budget);
        // To avoid huge arrays if amounts are large, scale down to whole rupees and clamp W.
        W = Math.min(W, 200000); // simple safety cap

        int[][] dp = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) {
            Payable p = payables.get(i - 1);
            int wt = (int)Math.round(p.amount);
            int val = p.impactScore;
            for (int w = 0; w <= W; w++) {
                dp[i][w] = dp[i - 1][w];
                if (wt <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - wt] + val);
                }
            }
        }
        // Reconstruct
        List<Payable> chosen = new ArrayList<>();
        int w = W;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Payable p = payables.get(i - 1);
                chosen.add(p);
                w -= (int)Math.round(p.amount);
                if (w < 0) break;
            }
        }
        Collections.reverse(chosen);
        return chosen;
    }
}

abstract class FinanceItem {
    public final int id;
    public final double amount;
    public final LocalDate dueDate;
    public FinanceItem(int id, double amount, LocalDate dueDate) {
        this.id = id; this.amount = amount; this.dueDate = dueDate;
    }
}

class Receivable extends FinanceItem {
    public Receivable(int id, double amount, LocalDate dueDate) { super(id, amount, dueDate); }
    @Override public String toString() {
        return String.format("[R#%d] Receive ₹%.2f by %s", id, amount, dueDate);
    }
}

class Payable extends FinanceItem {
    public final int impactScore; // subjective criticality 1..100
    public Payable(int id, double amount, LocalDate dueDate, int impactScore) {
        super(id, amount, dueDate); this.impactScore = impactScore;
    }
    @Override public String toString() {
        return String.format("[P#%d] Pay ₹%.2f by %s | impact=%d", id, amount, dueDate, impactScore);
    }
}

// ============================================================================
// CRM MODULE (Levenshtein dedupe)
// ============================================================================
class CRMManager {
    private int nextId = 1;
    private final List<Lead> leads = new ArrayList<>();

    public Lead addLead(Lead l) {
        Lead saved = new Lead(nextId++, l.name, l.email, l.phone);
        leads.add(saved);
        return saved;
    }

    public List<Lead> listLeads() { return new ArrayList<>(leads); }

    public List<Lead> findSimilar(Lead probe, int maxDistance) {
        List<Lead> res = new ArrayList<>();
        for (Lead l : leads) {
            int dName = Levenshtein.distance(l.name.toLowerCase(), probe.name.toLowerCase());
            int dEmail = Levenshtein.distance(l.email.toLowerCase(), probe.email.toLowerCase());
            if (Math.min(dName, dEmail) <= maxDistance) res.add(l);
        }
        return res;
    }
}

class Lead {
    public final int id; // 0 if not saved
    public final String name, email, phone;

    // constructor for probe/candidate
    public Lead(String name, String email, String phone) { this(0, name, email, phone); }
    public Lead(int id, String name, String email, String phone) {
        this.id = id; this.name = name; this.email = email; this.phone = phone;
    }
    @Override public String toString() {
        return String.format("[Lead#%d] %s | %s | %s", id, name, email, phone);
    }
}

class Levenshtein {
    public static int distance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[n][m];
    }
}

// ============================================================================
// WORKFLOW MODULE (DAG + Topological Sort + Critical Path)
// ============================================================================
class WorkflowManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, List<Integer>> adj = new HashMap<>(); // edges u->v (u must finish before v)

    public Task addTask(String name, int durationDays) {
        Task t = new Task(nextId++, name, durationDays);
        tasks.put(t.id, t);
        adj.put(t.id, new ArrayList<>());
        return t;
    }

    /** Add dependency: A depends on B  => edge B -> A */
    public boolean addDependency(int aId, int bId) {
        if (!tasks.containsKey(aId) || !tasks.containsKey(bId)) return false;
        // test for cycle by temporarily adding and checking topo
        adj.get(bId).add(aId);
        List<Task> topo = topologicalOrder();
        if (topo == null) { // cycle, revert
            adj.get(bId).remove((Integer)aId);
            return false;
        }
        return true;
    }

    public int taskCount() { return tasks.size(); }

    public List<Task> topologicalOrder() {
        Map<Integer, Integer> indeg = new HashMap<>();
        for (int id : tasks.keySet()) indeg.put(id, 0);
        for (Map.Entry<Integer, List<Integer>> e : adj.entrySet()) {
            for (int v : e.getValue()) indeg.put(v, indeg.get(v) + 1);
        }
        Deque<Integer> dq = new ArrayDeque<>();
        for (var en : indeg.entrySet()) if (en.getValue() == 0) dq.add(en.getKey());
        List<Task> order = new ArrayList<>();
        while (!dq.isEmpty()) {
            int u = dq.removeFirst();
            order.add(tasks.get(u));
            for (int v : adj.getOrDefault(u, Collections.emptyList())) {
                indeg.put(v, indeg.get(v) - 1);
                if (indeg.get(v) == 0) dq.add(v);
            }
        }
        if (order.size() != tasks.size()) return null; // cycle
        return order;
    }

    public static class CriticalPathResult {
        public final List<Task> path; public final int totalDuration;
        public CriticalPathResult(List<Task> path, int totalDuration) { this.path = path; this.totalDuration = totalDuration; }
    }

    /** Longest path in DAG by duration. */
    public CriticalPathResult criticalPath() {
        List<Task> topo = topologicalOrder();
        if (topo == null) return null; // cycle
        Map<Integer, Integer> dist = new HashMap<>(); // max duration to reach node
        Map<Integer, Integer> prev = new HashMap<>();
        for (Task t : topo) { dist.put(t.id, t.durationDays); prev.put(t.id, -1); }
        for (Task u : topo) {
            for (int v : adj.getOrDefault(u.id, Collections.emptyList())) {
                int cand = dist.get(u.id) + tasks.get(v).durationDays;
                if (cand > dist.get(v)) { dist.put(v, cand); prev.put(v, u.id); }
            }
        }
        // find max
        int bestId = -1, best = -1;
        for (var e : dist.entrySet()) if (e.getValue() > best) { best = e.getValue(); bestId = e.getKey(); }
        // reconstruct
        List<Task> path = new ArrayList<>();
        for (int cur = bestId; cur != -1; cur = prev.get(cur)) path.add(tasks.get(cur));
        Collections.reverse(path);
        return new CriticalPathResult(path, best);
    }
}

class Task {
    public final int id; public final String name; public final int durationDays;
    public Task(int id, String name, int durationDays) { this.id = id; this.name = name; this.durationDays = durationDays; }
    @Override public String toString() { return String.format("[Task#%d] %s (%d d)", id, name, durationDays); }
}
