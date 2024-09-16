package com.nighthawk.spring_portfolio.mvc.announcement;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/announcements")
public class AnnouncementControl {

    private final AnnouncementJPA announcementRepo;

    
    public AnnouncementControl(AnnouncementJPA announcementRepo) {
        this.announcementRepo = announcementRepo;
    }

    // Create Example
    @PostMapping("/create") 
    public ResponseEntity<Announcement> createAnnouncement( @RequestParam String author, @RequestParam String title, @RequestParam String body, @RequestParam String tags) {
        
        Announcement newAnnouncement = new Announcement(author, title, body, tags);
        Announcement savedAnnouncement = announcementRepo.save(newAnnouncement);
        
        return new ResponseEntity<>(savedAnnouncement, HttpStatus.CREATED);
    }

    // Read Example
    @GetMapping 
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepo.findAll();
        return new ResponseEntity<>(announcements, HttpStatus.OK);
    }

    // Update Example
    @PostMapping("/edit/{title}")
    public ResponseEntity<Announcement> editAnnouncement(@PathVariable String title, @RequestBody String body) {
        Announcement announcement = announcementRepo.findByTitle(title);
        announcement.setBody(body);
        announcementRepo.save(announcement);
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }

    // Delete Example
    @PostMapping("/delete/{title}")
    public ResponseEntity<HttpStatus> deleteAnnouncement(@PathVariable String title) {
        try {
            Announcement announcement = announcementRepo.findByTitle(title);
            announcementRepo.delete(announcement);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/author/{author}")
    public ResponseEntity<Announcement> getAnnouncementByAuthor(@PathVariable String author) {
        Announcement announcement = announcementRepo.findByAuthor(author);
        if (announcement != null) {
            return new ResponseEntity<>(announcement, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/tags/{tags}")
    public ResponseEntity<List<Announcement>> getAnnouncementsByTags(@PathVariable String tags) {
        List<Announcement> announcements = announcementRepo.findByTags(tags);
        return new ResponseEntity<>(announcements, HttpStatus.OK);
    }

    @GetMapping("/orderedByTime")
    public ResponseEntity<List<Announcement>> getAllAnnouncementsOrderedByTime() {
        List<Announcement> announcements = announcementRepo.findAllByOrderByTimestampDesc();
        return new ResponseEntity<>(announcements, HttpStatus.OK);
    }
}
