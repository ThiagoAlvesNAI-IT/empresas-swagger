package com.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.time.LocalDateTime;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class App {
    
    private List<Usuario> usuarios = new ArrayList<>();
    private Long nextId = 1L;
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    // Endpoint de health check
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
    
    // GET /usuarios - Listar usuários
    @GetMapping("/usuarios")
    public ResponseEntity<Map<String, Object>> getUsuarios(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", usuarios);
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", page);
        pagination.put("limit", limit);
        pagination.put("total", usuarios.size());
        pagination.put("totalPages", (int) Math.ceil((double) usuarios.size() / limit));
        
        response.put("pagination", pagination);
        
        return ResponseEntity.ok(response);
    }
    
    // POST /usuarios - Criar usuário
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioInput input) {
        Usuario usuario = new Usuario();
        usuario.setId(nextId++);
        usuario.setNome(input.getNome());
        usuario.setEmail(input.getEmail());
        usuario.setTelefone(input.getTelefone());
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
        
        usuarios.add(usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
    
    // GET /usuarios/{id} - Buscar usuário por ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarios.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
            
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PUT /usuarios/{id} - Atualizar usuário
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody UsuarioInput input) {
        Optional<Usuario> usuarioOpt = usuarios.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
            
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNome(input.getNome());
            usuario.setEmail(input.getEmail());
            usuario.setTelefone(input.getTelefone());
            usuario.setAtualizadoEm(LocalDateTime.now());
            
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /usuarios/{id} - Deletar usuário
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        boolean removed = usuarios.removeIf(u -> u.getId().equals(id));
        
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /auth/login - Login
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        // Simulação simples de login
        if ("admin@empresa.com".equals(request.getEmail()) && "123456".equals(request.getSenha())) {
            Map<String, Object> response = new HashMap<>();
            response.put("token", "jwt-token-exemplo-" + System.currentTimeMillis());
            
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("id", 1);
            usuario.put("nome", "Administrador");
            usuario.put("email", "admin@empresa.com");
            
            response.put("usuario", usuario);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

// Classes de modelo
class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}

class UsuarioInput {
    private String nome;
    private String email;
    private String telefone;
    
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}

class LoginRequest {
    private String email;
    private String senha;
    
    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
