/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fstg.eTaxe.Tnb.rest;

import com.fstg.eTaxe.Tnb.bean.Notification;
import com.fstg.eTaxe.Tnb.bean.Proprietaire;
import com.fstg.eTaxe.Tnb.bean.Terrain;
import com.fstg.eTaxe.Tnb.service.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author alikhyatti
 */
@RestController
@RequestMapping("/e-Taxe-Tnb/notification")
public class NotificationRest {

    @Autowired
    public NotificationService notificationService;

    //    tested
    @PostMapping(value = "/")
    public void save(@RequestBody Notification notification) {
        notificationService.save(notification);
    }

    //    tested
    @GetMapping("/libelle/{libelle}")
    public Notification findByLibelle(@PathVariable String libelle) {
        return notificationService.findByLibelle(libelle);
    }

//    @GetMapping("/id/{id}")
//    public Categorie findById(@PathVariable Long id){
//        return categorieService.findById(id);
//    }
    //    tested
    @DeleteMapping(value = "/id/{id}")
    public void deleteNotification(@PathVariable long id) {
        notificationService.deleteNotification(id);
    }

    //   tested
    @GetMapping(value="/")
    public List<Notification> findAll() {
        return notificationService.findAll();
    }
    
    /*@PutMapping(value="/id/{id}")
    public void update(@PathVariable long id, @RequestBody Notification notification){
        notificationService.update(id,notification);
    }
*/
    // alrady tested (yassine)
     @GetMapping("/proprietaire")
    public int HowMuchNotifierProprietaire(@RequestBody Proprietaire proprietaire) {
        return notificationService.HowMuchNotifierProprietaire(proprietaire);
    }
    // already test (yassine)
    @PostMapping("/numerdeNotification/{n}")
    public void NotifierTerrain( @PathVariable int n) {
        notificationService.NotifierTerrain(n);
    }

    public Notification findById(long id) {
        return notificationService.findById(id);
    }
    @GetMapping("/dateNow/{dateNow}/numeroNotification/{numeroNotification}")
    public void NotifierNow(@PathVariable int dateNow, @PathVariable int numeroNotification) {
        notificationService.NotifierNow(dateNow, numeroNotification);
    }

    public void NotifierUrgent(int annee) {
        notificationService.NotifierUrgent(annee);
    }
     
    public int NotifierTerrain(long id, int n) {
        return notificationService.NotifierTerrain(id, n);
    }
  @PutMapping("/id/{id}")
    public void updateNotification(@PathVariable long id) {
        notificationService.updateNotification(id);
    }
    
    
               
}
