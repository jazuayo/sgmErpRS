package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Lugar;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {

}
