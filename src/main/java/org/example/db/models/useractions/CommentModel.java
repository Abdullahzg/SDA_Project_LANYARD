package org.example.db.models.useractions;

import jakarta.persistence.*;
import org.example.db.models.trans.TransactionsModel;
import org.example.db.models.user.CustomerModel;

@Entity
@Table(name = "comments")
public class CommentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionsModel transaction;

    @Column(name = "comment", nullable = false)
    private String comment;

    public CommentModel() {
    }

    public CommentModel(CustomerModel customer, TransactionsModel transaction, String comment) {
        this.customer = customer;
        this.transaction = transaction;
        this.comment = comment;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public TransactionsModel getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionsModel transaction) {
        this.transaction = transaction;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}