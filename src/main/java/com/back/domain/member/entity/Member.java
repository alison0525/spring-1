package com.back.domain.member.entity;

import com.back.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    @Column(unique = true)
    private String apiKey;

    public Member(String username, String password, String nickname, String apiKey) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = apiKey;
    }

    public Member(int id, String username, String nickname){
        this.setId(id);
        this.username = username;
        this.nickname = nickname;
    }

    public String getName() {
        return this.nickname;
    }

    public boolean isAdmin(){
        return "admin".equals(this.username);
    }

    public List<SimpleGrantedAuthority> getAuthorities(){
        if(isAdmin()){
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }else{
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
}
